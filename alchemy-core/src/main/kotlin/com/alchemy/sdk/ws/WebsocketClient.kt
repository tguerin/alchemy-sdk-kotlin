package com.alchemy.sdk.ws

import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import okio.ByteString
import java.util.concurrent.TimeUnit

class WebsocketClient(
    private val websocketUrl: String,
    private val okHttpClientBuilder: OkHttpClient.Builder
) {

    fun on(event: String, numberOfEvents: Int = NO_EVENT_RESTRICTION): Flow<String> {
        return callbackFlow {
            var numberOfEmittedEvents = 0
            val websocket = AutoConnectWebsocket(
                okHttpClientBuilder
                    .readTimeout(0, TimeUnit.MILLISECONDS)
                    .build(),
                Request.Builder()
                    .url(websocketUrl)
                    .build(),
                object : WebSocketListener() {
                    override fun onMessage(webSocket: WebSocket, text: String) {
                        if (numberOfEvents == NO_EVENT_RESTRICTION || ++numberOfEmittedEvents <= numberOfEvents) {
                            channel.trySend(text)
                        } else {
                            channel.close()
                        }
                    }

                    override fun onMessage(webSocket: WebSocket, bytes: ByteString) {
                        if (numberOfEvents == NO_EVENT_RESTRICTION || ++numberOfEmittedEvents <= numberOfEvents) {
                            channel.trySend(bytes.toAsciiUppercase().toString())
                        } else {
                            channel.close()
                        }
                    }
                }
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
}