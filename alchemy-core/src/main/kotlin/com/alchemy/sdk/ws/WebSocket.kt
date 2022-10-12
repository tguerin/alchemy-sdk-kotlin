package com.alchemy.sdk.ws

import com.alchemy.sdk.core.Core
import com.alchemy.sdk.core.model.Address
import com.alchemy.sdk.json.rpc.client.generator.IdGenerator
import com.alchemy.sdk.json.rpc.client.model.JsonRpcException
import com.alchemy.sdk.json.rpc.client.model.JsonRpcRequest
import com.alchemy.sdk.util.HexString
import com.alchemy.sdk.util.HexString.Companion.hexString
import com.alchemy.sdk.util.pmap
import com.alchemy.sdk.ws.WebSocket.MessageWithMetadata.Companion.DefaultSubscriptionId
import com.alchemy.sdk.ws.model.PendingTransaction
import com.alchemy.sdk.ws.model.WebSocketJsonRpcResponse
import com.alchemy.sdk.ws.model.WebsocketEvent
import com.alchemy.sdk.ws.model.WebsocketMethod
import com.alchemy.sdk.ws.model.WebsocketStatus
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.google.gson.stream.JsonToken
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import java.io.StringReader

private fun <T> Flow<WebsocketEvent>.dataOnly(): Flow<Result<T>> {
    return this.filterIsInstance<WebsocketEvent.Data<Result<T>>>().map { it.data }
}

@Suppress("UNCHECKED_CAST", "ThrowableNotThrown")
class WebSocket internal constructor(
    private val idGenerator: IdGenerator,
    private val core: Core,
    private val gson: Gson,
    websocketUrl: String,
    okHttpClientBuilder: OkHttpClient.Builder
) {

    private val coroutineScope = CoroutineScope(Dispatchers.IO + CoroutineName("WebSocket"))

    private val flowCache = mutableMapOf<WebsocketMethod<*>, Flow<*>>()
    private val mapMethodBySubscription = mutableMapOf<HexString, WebsocketMethod<*>>()
    private val mapSubscriptionByMethodId = mutableMapOf<String, HexString>()

    private val websocketConnection = WebSocketConnection(websocketUrl, okHttpClientBuilder)

    val status by lazy {
        websocketConnection.status
            .map { it.status }
            .stateIn(
                scope = coroutineScope,
                started = SharingStarted.WhileSubscribed(),
                initialValue = WebsocketStatus.Disconnected
            )
            .distinctUntilChanged { old, new -> old == new }
    }

    private val connectionTrigger = flowOf(true)
        .shareIn(
            scope = coroutineScope,
            replay = 1,
            started = SharingStarted.WhileSubscribed(),
        )
        .onStart {
            websocketConnection.connect()
        }
        .onCompletion {
            websocketConnection.close(1000, "no subscribers")
        }

    private val reconnectionAwareFlow by lazy {
        websocketConnection.flow
            .map(::parseMetadata)
            .mapNotNull(::parse)
            .shareIn(
                scope = coroutineScope,
                started = SharingStarted.WhileSubscribed()
            )
    }

    private fun parseMetadata(rawMessage: WebsocketEvent.RawMessage): MessageWithMetadata {
        val subscriptionMatchResult = when {
            rawMessage.message.contains("subscription") -> {
                subscriptionRegex.find(rawMessage.message)
            }
            rawMessage.message.contains("result") -> {
                resultRegex.find(rawMessage.message)
            }
            else -> {
                null
            }
        }
        val idMatchResult = idRegex.find(rawMessage.message)
        return MessageWithMetadata(
            rawMessage.message,
            idMatchResult?.groupValues?.getOrNull(1) ?: "",
            subscriptionMatchResult?.groupValues?.getOrNull(1)?.hexString ?: DefaultSubscriptionId
        )
    }

    private fun updateSubscriptionState(
        event: WebsocketEvent.Subscription,
        method: WebsocketMethod<*>
    ) {
        mapMethodBySubscription[event.subscriptionId] = method
        mapSubscriptionByMethodId[event.methodId] = event.subscriptionId
    }

    private fun parse(messageWithMetadata: MessageWithMetadata): WebsocketEvent? {
        return when {
            messageWithMetadata.methodId.isNotEmpty() && messageWithMetadata.subscriptionId != DefaultSubscriptionId -> {
                WebsocketEvent.Subscription(
                    messageWithMetadata.methodId,
                    messageWithMetadata.subscriptionId
                )
            }
            messageWithMetadata.methodId.isNotEmpty() &&
                    (messageWithMetadata.message.contains("true")
                            || messageWithMetadata.message.contains("false")) -> {
                WebsocketEvent.UnSubscription(
                    messageWithMetadata.methodId,
                    messageWithMetadata.message.contains("true")
                )
            }
            messageWithMetadata.subscriptionId != DefaultSubscriptionId &&
                    messageWithMetadata.message.contains("params") -> {
                WebsocketEvent.Data(
                    messageWithMetadata.subscriptionId,
                    parseWebSocketResponseContent(
                        mapMethodBySubscription[messageWithMetadata.subscriptionId]?.responseType!!,
                        messageWithMetadata.message
                    )
                )
            }
            else -> null // TODO log something
        }
    }

    suspend fun <T> on(method: WebsocketMethod<T>): Flow<Result<T>> = with(Dispatchers.IO) {
        if (method is WebsocketMethod.Transaction) {
            val receiptResult = core.getTransactionReceipt(method.hash)
            return if (receiptResult.isSuccess && receiptResult.getOrThrow() != null) {
                flowOf(receiptResult)
            } else {
                flowCache.getOrPut(method) {
                    on(WebsocketMethod.Block)
                        .map {
                            core.getTransactionReceipt(method.hash)
                        }
                        .filter { receiptResult ->
                            receiptResult.isSuccess && receiptResult.getOrThrow() != null
                        }
                }
            } as Flow<Result<T>>
        }
        val resolvedMethod = resolveMethod(method)
        return flowCache.getOrPut(resolvedMethod) {
            val methodId = idGenerator.generateId()
            connectionTrigger
                .flatMapLatest {
                    status
                }
                .filter { it == WebsocketStatus.Connected }
                .distinctUntilChanged()
                .onEach {
                    subscribeToEvent(methodId, resolvedMethod)
                }
                .flatMapLatest {
                    reconnectionAwareFlow
                        .onEach { event ->
                            if (event is WebsocketEvent.Subscription && methodId == event.methodId) {
                                updateSubscriptionState(event, method)
                            }
                        }
                        .filterIsInstance<WebsocketEvent.Data<*>>()
                        .onCompletion {
                            coroutineScope.launch {
                                mapSubscriptionByMethodId[methodId]?.let { subscriptionId ->
                                    unsubscribeFromEvent(methodId, subscriptionId)
                                }
                            }
                        }

                }
                .shareIn(
                    coroutineScope,
                    started = SharingStarted.WhileSubscribed(),
                    replay = 1
                )
                .dataOnly<T>()
                .filter {
                    filterResponse(resolvedMethod, it)
                }
        } as Flow<Result<T>>
    }

    private suspend fun unsubscribeFromEvent(methodId: String, subscriptionId: HexString) {
        mapMethodBySubscription.remove(subscriptionId)
        mapSubscriptionByMethodId.remove(methodId)
        websocketConnection.send(
            forgeEvent(
                idGenerator.generateId(),
                WebsocketMethod.UnSubscribe(subscriptionId)
            )
        )
    }

    private suspend fun <T> subscribeToEvent(
        methodId: String,
        method: WebsocketMethod<T>
    ) {
        websocketConnection.send(forgeEvent(methodId, method))
    }

    private fun <T> parseWebSocketResponseContent(
        type: Class<T>,
        message: String
    ): Result<T> {
        val typeToken = TypeToken.getParameterized(
            WebSocketJsonRpcResponse::class.java, type
        ) as TypeToken<WebSocketJsonRpcResponse<T>>
        val (content, exception) = parseContent(typeToken, message)
        return when {
            content != null && content.params.result != null -> {
                Result.success(content.params.result)
            }
            content != null && content.params.error != null -> {
                Result.failure(JsonRpcException(content.params.error))
            }
            exception != null -> {
                Result.failure(exception)
            }
            else -> {
                Result.failure(RuntimeException("can't happen"))
            }
        }
    }

    private fun <T> parseContent(
        typeToken: TypeToken<T>, message: String
    ): Pair<T?, Throwable?> {
        val jsonReader = gson.newJsonReader(StringReader(message))
        val adapter = gson.getAdapter(typeToken)
        var dataRead: T?
        var exception: Throwable? = null
        try {
            dataRead = adapter.read(jsonReader)
            if (jsonReader.peek() !== JsonToken.END_DOCUMENT) {
                dataRead = null
                exception = RuntimeException("Json Parsing Failed")
            }
        } catch (e: Exception) {
            dataRead = null
            exception = e
        }
        return Pair(dataRead, exception)
    }

    private suspend fun <T> forgeEvent(id: String, method: WebsocketMethod<T>): String {
        return gson.toJson(
            JsonRpcRequest(
                id = id, method = method.name, params = resolveParam(method.params)
            )
        )
    }

    private fun <T> filterResponse(method: WebsocketMethod<T>, result: Result<T>): Boolean {
        return when {
            method is WebsocketMethod.PendingTransactions && result.isSuccess -> {
                val pendingTransaction = result.getOrThrow()
                if (pendingTransaction is PendingTransaction.FullPendingTransaction) {
                    val validFrom =
                        method.fromAddress == null || pendingTransaction.from == method.fromAddress
                    val validTo =
                        method.toAddresses.isEmpty() || method.toAddresses.map { it.value }
                            .contains(pendingTransaction.to)
                    validFrom && validTo
                } else {
                    true
                }
            }
            else -> true
        }
    }

    private suspend fun <T> resolveMethod(method: WebsocketMethod<T>): WebsocketMethod<T> {
        return when (method) {
            is WebsocketMethod.PendingTransactions -> {
                method.copy(
                    fromAddress = if (method.fromAddress != null) {
                        core.resolveAddress(method.fromAddress).getOrThrow()
                    } else {
                        null
                    }, toAddresses = resolveParam(method.toAddresses) as List<Address>
                ) as WebsocketMethod<T>
            }
            is WebsocketMethod.LogFilter -> {
                method.copy(
                    address = if (method.address != null) {
                        core.resolveAddress(method.address).getOrThrow()
                    } else {
                        null
                    }
                ) as WebsocketMethod<T>
            }
            else -> {
                method
            }
        }

    }

    private suspend fun resolveParam(params: List<Any?>): List<Any?> {
        return params.pmap { param ->
            when (param) {
                is Address -> {
                    core.resolveAddress(param).getOrThrow()
                }
                is List<Any?> -> {
                    resolveParam(param)
                }
                else -> {
                    param
                }
            }
        }
    }

    /** For test purpose only */
    internal fun emit(message: String) {
        websocketConnection.emit(message)
    }

    /** For test purpose only*/
    internal fun close(code: Int, message: String) {
        websocketConnection.close(code, message)
    }

    /** For test purpose only*/
    internal fun connect() {
        websocketConnection.connect()
    }

    private class MessageWithMetadata(
        val message: String = "",
        val methodId: String = "",
        val subscriptionId: HexString = DefaultSubscriptionId
    ) {
        companion object {
            val DefaultSubscriptionId = "0x0".hexString
        }
    }


    companion object {
        private val subscriptionRegex = "\"subscription\":\"(0x[0-9a-fA-F]+)\"".toRegex()
        private val resultRegex = "\"result\":\"(0x[0-9a-fA-F]+)\"".toRegex()
        private val idRegex = "\"id\":\"([0-9]+)\".*".toRegex()
    }
}