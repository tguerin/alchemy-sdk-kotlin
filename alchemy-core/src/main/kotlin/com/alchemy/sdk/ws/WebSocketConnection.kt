package com.alchemy.sdk.ws

import com.alchemy.sdk.ws.model.WebsocketEvent
import com.alchemy.sdk.ws.model.WebsocketStatus
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.stateIn
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

    private val coroutineScope = CoroutineScope(Dispatchers.IO)

    private val responseFlow = MutableStateFlow(WebsocketEvent.RawMessage(""))

    private val statusFlow = MutableStateFlow(WebsocketEvent.Status(WebsocketStatus.Disconnected))

    private val websocket = AutoConnectWebsocket(
        okHttpClient = okHttpClientBuilder
            .readTimeout(0, TimeUnit.MILLISECONDS)
            .build(),
        request = Request.Builder()
            .url(websocketUrl)
            .build(),
        listener = ChannelWebsocketListener(),
        onConnectStatusChangeListener = ConnectionStatusListener()
    )

    val flow = responseFlow
        .filter(::ignoreFirstEmptyMessage)
        .stateIn(
            scope = coroutineScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = WebsocketEvent.RawMessage("")
        )

    val status = statusFlow
        .distinctUntilChanged { old, new -> old == new }
        .stateIn(
            scope = coroutineScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = WebsocketEvent.Status(WebsocketStatus.Disconnected)
        )

    private fun ignoreFirstEmptyMessage(event: WebsocketEvent) =
        !(event is WebsocketEvent.RawMessage && event.message.isEmpty())

    fun send(event: String) {
        websocket.send(event)
    }

    private inner class ConnectionStatusListener : AutoConnectWebsocket.ConnectionStatusListener {
        override fun invoke(webSocket: WebSocket, status: WebsocketStatus) {
            coroutineScope.launch {
                statusFlow.tryEmit(WebsocketEvent.Status(status))
            }
        }
    }

    private inner class ChannelWebsocketListener : WebSocketListener() {

        override fun onMessage(webSocket: WebSocket, text: String) {
            coroutineScope.launch {
                responseFlow.tryEmit(WebsocketEvent.RawMessage(text))
            }
        }

        override fun onMessage(webSocket: WebSocket, bytes: ByteString) {
            coroutineScope.launch {
                responseFlow.tryEmit(WebsocketEvent.RawMessage(bytes.toAsciiUppercase().toString()))
            }
        }

    }
}