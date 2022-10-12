package com.alchemy.sdk.ws

import com.alchemy.sdk.ws.model.WebsocketEvent
import com.alchemy.sdk.ws.model.WebsocketStatus
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import okio.ByteString
import java.util.concurrent.TimeUnit

internal class WebSocketConnection(
    websocketUrl: String,
    okHttpClientBuilder: OkHttpClient.Builder
) {

    private val coroutineScope = CoroutineScope(Dispatchers.IO + CoroutineName("WebSocketConnection"))

    private val responseFlow = MutableSharedFlow<WebsocketEvent.RawMessage>(replay = 1)

    private val statusFlow = MutableStateFlow(WebsocketEvent.Status(WebsocketStatus.Disconnected))

    private val websocket by lazy {
        AutoConnectWebSocket(
            okHttpClient = okHttpClientBuilder
                .readTimeout(0, TimeUnit.MILLISECONDS)
                .build(),
            request = Request.Builder()
                .url(websocketUrl)
                .build(),
            listener = ChannelWebsocketListener(),
            onConnectStatusChangeListener = ConnectionStatusListener()
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

    fun emit(message: String) {
        websocket.emit(message)
    }

    fun close(code: Int, message: String) {
        websocket.close(code, message)
    }

    fun connect() {
        websocket.connect()
    }

    private inner class ConnectionStatusListener : AutoConnectWebSocket.ConnectionStatusListener {
        override fun invoke(webSocket: WebSocket, status: WebsocketStatus) {
            coroutineScope.launch {
                statusFlow.emit(WebsocketEvent.Status(status))
            }
        }
    }

    private inner class ChannelWebsocketListener : WebSocketListener() {

        override fun onMessage(webSocket: WebSocket, text: String) {
            coroutineScope.launch {
                responseFlow.emit(WebsocketEvent.RawMessage(text))
            }
        }

        override fun onMessage(webSocket: WebSocket, bytes: ByteString) {
            coroutineScope.launch {
                responseFlow.emit(WebsocketEvent.RawMessage(bytes.toAsciiUppercase().toString()))
            }
        }

    }
}