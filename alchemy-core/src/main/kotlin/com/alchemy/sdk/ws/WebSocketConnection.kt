package com.alchemy.sdk.ws

import com.alchemy.sdk.ws.model.WebsocketEvent
import com.alchemy.sdk.ws.model.WebsocketStatus
import io.ktor.client.HttpClient
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch

internal class WebSocketConnection(
    websocketUrl: String,
    httpClient: HttpClient,
    retryPolicy: RetryPolicy
) {

    private val coroutineScope = CoroutineScope(Dispatchers.IO + CoroutineName("WebSocketConnection"))

    private val responseFlow = MutableSharedFlow<WebsocketEvent.RawMessage>(replay = 1)

    private val statusFlow = MutableStateFlow(WebsocketEvent.Status(WebsocketStatus.Disconnected))

    private val websocket by lazy {
        AutoConnectWebSocket(
            httpClient = httpClient,
            websocketUrl = websocketUrl,
            onMessage = { text ->
                coroutineScope.launch {
                    responseFlow.emit(WebsocketEvent.RawMessage(text))
                }
            },
            onConnectStatusChangeListener = ConnectionStatusListener(),
            retryPolicy = retryPolicy
        )
    }

    val flow = responseFlow
        .filter {
            it.message.isNotEmpty()
        }

    val status = statusFlow

    fun send(event: String) {
        websocket.send(event)
    }

    fun close(code: Short, message: String) {
        websocket.close(code, message)
    }

    fun connect() {
        websocket.connect()
    }

    private inner class ConnectionStatusListener : AutoConnectWebSocket.ConnectionStatusListener {
        override fun invoke(status: WebsocketStatus) {
            coroutineScope.launch {
                statusFlow.emit(WebsocketEvent.Status(status))
            }
        }
    }
}