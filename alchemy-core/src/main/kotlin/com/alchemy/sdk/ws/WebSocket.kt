package com.alchemy.sdk.ws

import com.alchemy.sdk.core.Core
import com.alchemy.sdk.core.model.Address
import com.alchemy.sdk.json.rpc.client.generator.IdGenerator
import com.alchemy.sdk.json.rpc.client.model.JsonRpcException
import com.alchemy.sdk.json.rpc.client.model.JsonRpcRequest
import com.alchemy.sdk.json.rpc.client.model.JsonRpcResponse
import com.alchemy.sdk.util.HexString
import com.alchemy.sdk.util.pmap
import com.alchemy.sdk.ws.model.PendingTransaction
import com.alchemy.sdk.ws.model.WebSocketJsonRpcResponse
import com.alchemy.sdk.ws.model.WebsocketEvent
import com.alchemy.sdk.ws.model.WebsocketMethod
import com.alchemy.sdk.ws.model.WebsocketStatus
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.google.gson.stream.JsonToken
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onSubscription
import kotlinx.coroutines.flow.stateIn
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

    private val coroutineScope = CoroutineScope(Dispatchers.IO)

    private val mapSubscriptionByMethod = mutableMapOf<WebsocketMethod<*>, Subscription>()

    private val websocketConnection = WebSocketConnection(websocketUrl, okHttpClientBuilder)

    val status by lazy {
        websocketConnection.status.map { it.status }
            .stateIn(
                scope = coroutineScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = WebsocketStatus.Disconnected
            )
            .distinctUntilChanged { old, new -> old == new }
    }

    private val reconnectionAwareFlow = websocketConnection.flow
        .combine(status) { data, status ->
            resubscribeIfNecessary(status)
            data
        }
        .stateIn(
            scope = coroutineScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = WebsocketEvent.RawMessage("")
        )

    private suspend fun resubscribeIfNecessary(status: WebsocketStatus) {
        if (status == WebsocketStatus.Reconnected) {
            mapSubscriptionByMethod.keys.forEach {
                // Reconnect all subscriptions
                websocketConnection.send(forgeEvent(it))
                // TODO here we could backfill missing data
            }
        }
    }


    private fun ignoreFirstEmptyMessage(event: WebsocketEvent) =
        !(event is WebsocketEvent.RawMessage && event.message.isEmpty())

    suspend fun <T> on(method: WebsocketMethod<T>): Flow<Result<T>> {
        val resolvedMethod = resolveMethod(method)
        return reconnectionAwareFlow
            .onSubscription {
                subscribeToEventOnce(resolvedMethod)
            }
            .parseResponse(resolvedMethod)
            .dataOnly<T>()
            .filter {
                filterResponse(resolvedMethod, it)
            }
            .catch { e ->
                Result.failure<T>(e)
            }
    }

    private fun <T> Flow<WebsocketEvent.RawMessage>.parseResponse(
        method: WebsocketMethod<T>
    ): Flow<WebsocketEvent> {
        return this
            .filter(::ignoreFirstEmptyMessage)
            .mapNotNull { event ->
                parse(method, event)
            }
            .onEach { event ->
                updateSubscriptionId(method, event)
            }
            .onCompletion {
                unsubscribeIfNoSubscribers(method)
            }
    }

    private fun <T> parse(
        method: WebsocketMethod<T>,
        event: WebsocketEvent.RawMessage
    ): WebsocketEvent? {
        return when {
            event.message.contains("params") -> {
                WebsocketEvent.Data(parseWebSocketResponseContent(method.responseType, event))
            }
            event.message.contains("true") || event.message.contains("false") -> {
                WebsocketEvent.UnSubscription(event.message.contains("true"))
            }
            else -> {
                val subscriptionId = parseSubscriptionContent(event)
                if (subscriptionId != null) {
                    WebsocketEvent.Subscription(subscriptionId)
                } else {
                    null
                }
            }
        }
    }

    private suspend fun <T> unsubscribeIfNoSubscribers(method: WebsocketMethod<T>) {
        mapSubscriptionByMethod[method]?.let { subscription ->
            subscription.subscriptionCount -= 1
            if (subscription.subscriptionCount == 0) {
                mapSubscriptionByMethod.remove(method)
                subscription.subscriptionId?.let { subscriptionId ->
                    websocketConnection.send(forgeEvent(WebsocketMethod.UnSubscribe(subscriptionId)))
                }
            }
        }
    }

    private suspend fun <T> subscribeToEventOnce(
        method: WebsocketMethod<T>
    ) {
        mapSubscriptionByMethod.getOrPut(method) {
            websocketConnection.send(forgeEvent(method))
            Subscription(null, 0)
        }.also {
            it.subscriptionCount += 1
        }
    }

    private fun <T> updateSubscriptionId(
        method: WebsocketMethod<T>,
        event: WebsocketEvent
    ) {
        if (event is WebsocketEvent.Subscription) {
            mapSubscriptionByMethod[method]?.subscriptionId = event.id
        }
    }

    private fun parseSubscriptionContent(
        event: WebsocketEvent.RawMessage
    ): HexString? {
        val typeToken = TypeToken.getParameterized(
            JsonRpcResponse::class.java,
            HexString::class.java
        ) as TypeToken<JsonRpcResponse<HexString>>
        val (content, _) = parseContent(typeToken, event)
        return content?.result
    }

    private fun <T> parseWebSocketResponseContent(
        type: Class<T>,
        event: WebsocketEvent.RawMessage
    ): Result<T> {
        val typeToken = TypeToken.getParameterized(
            WebSocketJsonRpcResponse::class.java,
            type
        ) as TypeToken<WebSocketJsonRpcResponse<T>>
        val (content, exception) = parseContent(typeToken, event)
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
        typeToken: TypeToken<T>,
        event: WebsocketEvent.RawMessage
    ): Pair<T?, Throwable?> {
        val jsonReader = gson.newJsonReader(StringReader(event.message))
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

    private suspend fun <T> forgeEvent(method: WebsocketMethod<T>): String {
        return gson.toJson(
            JsonRpcRequest(
                id = idGenerator.generateId(),
                method = method.name,
                params = resolveParam(method.params)
            )
        )
    }

    private fun <T> filterResponse(method: WebsocketMethod<T>, result: Result<T>): Boolean {
        return when {
            method is WebsocketMethod.PendingTransactions && result.isSuccess -> {
                val pendingTransaction = result.getOrThrow()
                if (pendingTransaction is PendingTransaction.FullPendingTransaction) {
                    val validFrom = method.fromAddress == null ||pendingTransaction.from == method.fromAddress
                    val validTo = method.toAddresses.isEmpty() || method.toAddresses.map { it.value }.contains(pendingTransaction.to)
                    validFrom && validTo
                } else {
                    true
                }
            }
            else -> true
        }
    }
    private suspend fun <T> resolveMethod(method: WebsocketMethod<T>): WebsocketMethod<T> {
        return if (method is WebsocketMethod.PendingTransactions) {
            method.copy(
                fromAddress = if (method.fromAddress != null) {
                    core.resolveAddress(method.fromAddress).getOrThrow()
                } else {
                    null
                },
                toAddresses = resolveParam(method.toAddresses) as List<Address>
            ) as WebsocketMethod<T>
        } else {
            method
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

    /** For test purpose */
    fun emit(message: String) {
        websocketConnection.emit(message)
    }

    private class Subscription(
        var subscriptionId: HexString? = null,
        var subscriptionCount: Int
    )

}