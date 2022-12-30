package com.alchemy.sdk.ws

import com.alchemy.sdk.core.api.CoreApi
import com.alchemy.sdk.rpc.model.JsonRpcException
import com.alchemy.sdk.rpc.model.JsonRpcRequest
import com.alchemy.sdk.util.Dispatchers
import com.alchemy.sdk.util.HexString
import com.alchemy.sdk.util.HexString.Companion.hexString
import com.alchemy.sdk.util.generator.IdGenerator
import com.alchemy.sdk.ws.WebSocket.MessageWithMetadata.Companion.DefaultSubscriptionId
import com.alchemy.sdk.ws.model.PendingTransaction
import com.alchemy.sdk.ws.model.WebSocketJsonRpcResponse
import com.alchemy.sdk.ws.model.WebsocketEvent
import com.alchemy.sdk.ws.model.WebsocketMethod
import com.alchemy.sdk.ws.model.WebsocketStatus
import io.ktor.client.HttpClient
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
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
import kotlinx.coroutines.withContext
import kotlinx.serialization.KSerializer
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray

private fun <T> Flow<WebsocketEvent>.dataOnly(): Flow<Result<T>> {
    return this.filterIsInstance<WebsocketEvent.Data<Result<T>>>().map { it.data }
}

@Suppress("UNCHECKED_CAST", "ThrowableNotThrown")
class WebSocket internal constructor(
    private val idGenerator: IdGenerator,
    private val core: CoreApi,
    private val json: Json,
    private val dispatchers: Dispatchers,
    retryPolicy: RetryPolicy,
    websocketUrl: String,
    httpClient: HttpClient,
) {

    // TODO use custom io dispatcher
    private val coroutineScope = CoroutineScope(dispatchers.io + CoroutineName("WebSocket"))
    private val singleThreadDispatcher =
        (dispatchers.io.limitedParallelism(1) + CoroutineName("Websocket single thread"))

    private val flowCache = mutableMapOf<WebsocketMethod<*>, Flow<*>>()
    private val mapMethodBySubscription = mutableMapOf<HexString, WebsocketMethod<*>>()
    private val mapSubscriptionByMethodId = mutableMapOf<String, HexString>()

    private val websocketConnection = WebSocketConnection(
        dispatchers,
        websocketUrl,
        httpClient,
        retryPolicy,
    )

    val statusFlow by lazy {
        websocketConnection.status
            .map { it.status }
            .stateIn(
                scope = coroutineScope,
                started = SharingStarted.WhileSubscribed(),
                initialValue = WebsocketStatus.Disconnected
            )
            .distinctUntilChanged { old, new -> old == new }
    }

    private val connectionTriggerFlow = flowOf(true)
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

            messageWithMetadata.subscriptionId != DefaultSubscriptionId &&
                    messageWithMetadata.message.contains("params") &&
                    mapMethodBySubscription[messageWithMetadata.subscriptionId] != null -> {
                WebsocketEvent.Data(
                    messageWithMetadata.subscriptionId,
                    parseWebSocketResponseContent(
                        mapMethodBySubscription[messageWithMetadata.subscriptionId]?.serializer!!,
                        messageWithMetadata.message
                    )
                )
            }

            else -> null // TODO log something
        }
    }

    // TODO use custom dispatchers io
    suspend fun <T> on(method: WebsocketMethod<T>): Flow<Result<T>> = withContext(dispatchers.io) {
        if (method is WebsocketMethod.Transaction) {
            val receiptResult = core.getTransactionReceipt(method.hash)
            return@withContext if (receiptResult.isSuccess && receiptResult.getOrThrow() != null) {
                flowOf(receiptResult)
            } else {
                withContext(singleThreadDispatcher) {
                    flowCache.getOrPut(method) {
                        on(WebsocketMethod.Block)
                            .map {
                                core.getTransactionReceipt(method.hash)
                            }
                            .filter { receiptResult ->
                                receiptResult.isSuccess && receiptResult.getOrThrow() != null
                            }
                    }
                }
            } as Flow<Result<T>>
        }
        val resolvedMethod = resolveMethod(method)
        return@withContext withContext(singleThreadDispatcher) {
            flowCache.getOrPut(resolvedMethod) {
                val methodId = idGenerator.generateId()
                connectionTriggerFlow
                    .flatMapLatest {
                        statusFlow
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
    }

    private fun unsubscribeFromEvent(methodId: String, subscriptionId: HexString) {
        mapMethodBySubscription.remove(subscriptionId)
        mapSubscriptionByMethodId.remove(methodId)
        websocketConnection.send(
            forgeEvent(
                idGenerator.generateId(),
                WebsocketMethod.UnSubscribe(subscriptionId)
            )
        )
    }

    private fun <T> subscribeToEvent(
        methodId: String,
        method: WebsocketMethod<T>
    ) {
        websocketConnection.send(forgeEvent(methodId, method))
    }

    private fun <T> parseWebSocketResponseContent(
        innerTypeSerializer: KSerializer<T>,
        message: String
    ): Result<T> {
        val content: WebSocketJsonRpcResponse<T> = try {
            json.decodeFromString(WebSocketJsonRpcResponse.serializer(innerTypeSerializer), message)
        } catch (e: Exception) {
            return Result.failure(e)
        }
        return when {
            content.params.result != null -> {
                Result.success(content.params.result)
            }

            content.params.error != null -> {
                Result.failure(JsonRpcException(content.params.error))
            }

            else -> {
                error("Websocket response should have either result or error field")
            }
        }
    }

    private fun <T> forgeEvent(id: String, method: WebsocketMethod<T>): String {
        return json.encodeToString(
            JsonRpcRequest(
                id = id,
                method = method.name,
                params = JsonArray(method.params.map {
                    json.encodeToJsonElement(
                        it.second as KSerializer<Any>,
                        it.first
                    )
                })
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
        return method.resolveAddresses(core)

    }

    /** For test purpose only */
    internal fun emit(message: String) {
        websocketConnection.send(message)
    }

    /** For test purpose only*/
    internal fun close(code: Short, message: String) {
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