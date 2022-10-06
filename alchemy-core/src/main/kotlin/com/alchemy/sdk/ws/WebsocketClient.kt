package com.alchemy.sdk.ws

import com.alchemy.sdk.ws.model.WebsocketEvent
import com.alchemy.sdk.ws.model.WebsocketStatus
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import okio.ByteString
import java.util.concurrent.TimeUnit

internal class WebsocketClient(
    private val websocketUrl: String,
    private val okHttpClientBuilder: OkHttpClient.Builder
) {

    fun on(
        event: String,
        numberOfEvents: Int = NO_EVENT_RESTRICTION
    ): Flow<WebsocketEvent<String>> {
        return callbackFlow {
            val websocket = AutoConnectWebsocket(
                okHttpClient = okHttpClientBuilder
                    .readTimeout(0, TimeUnit.MILLISECONDS)
                    .build(),
                request = Request.Builder()
                    .url(websocketUrl)
                    .build(),
                listener = ChannelWebsocketListener(channel, numberOfEvents),
                onConnectStatusChangeListener = ConnectionStatusListener(channel)
            )
            websocket.send(event)
            awaitClose {
                websocket.close(1000, null)
            }
        }
    }

    companion object {
        private const val NO_EVENT_RESTRICTION = 0
    }

    private class ConnectionStatusListener(
        private val sendChannel: SendChannel<WebsocketEvent<String>>
    ) : AutoConnectWebsocket.ConnectionStatusListener {
        override fun invoke(status: WebsocketStatus) {
            sendChannel.trySend(WebsocketEvent.Status(status))
        }
    }

    private class ChannelWebsocketListener(
        private val channel: SendChannel<WebsocketEvent<String>>,
        private val numberOfEvents: Int = NO_EVENT_RESTRICTION
    ) : WebSocketListener() {

        private var numberOfEmittedEvents = 0

        override fun onMessage(webSocket: WebSocket, text: String) {
            if (numberOfEvents == NO_EVENT_RESTRICTION || ++numberOfEmittedEvents <= numberOfEvents) {
                channel.trySend(WebsocketEvent.Data(text))
            } else {
                channel.close()
            }
        }

        override fun onMessage(webSocket: WebSocket, bytes: ByteString) {
            if (numberOfEvents == NO_EVENT_RESTRICTION || ++numberOfEmittedEvents <= numberOfEvents) {
                channel.trySend(WebsocketEvent.Data(bytes.toAsciiUppercase().toString()))
            } else {
                channel.close()
            }
        }

    }
}