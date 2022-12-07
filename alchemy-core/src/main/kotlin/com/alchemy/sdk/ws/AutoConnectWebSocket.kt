package com.alchemy.sdk.ws

import com.alchemy.sdk.ws.model.WebsocketStatus
import io.ktor.client.HttpClient
import io.ktor.client.plugins.websocket.webSocket
import io.ktor.client.request.url
import io.ktor.websocket.CloseReason
import io.ktor.websocket.Frame
import io.ktor.websocket.readText
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch

internal class AutoConnectWebSocket(
    private val httpClient: HttpClient,
    private val websocketUrl: String,
    private val onMessage: (String) -> Unit,
    private val onConnectStatusChangeListener: ConnectionStatusListener,
    private val retryPolicy: RetryPolicy,
) {

    sealed interface OutgoingMessage {
        class DataMessage(val message: String) : OutgoingMessage
        class CloseMessage(val closeCode: Short, val message: String) : OutgoingMessage
    }

    private val coroutineScope = CoroutineScope(Dispatchers.IO + CoroutineName("Ws context"))

    private val outgoingMessageFlow = MutableSharedFlow<OutgoingMessage>()

    /**
     * get the status of reconnection
     */
    var status: WebsocketStatus = WebsocketStatus.Disconnected
        private set(value) {
            if (field != value) {
                field = value
                onConnectStatusChangeListener(value)
            }
        }


    fun connect() {
        coroutineScope.launch {
            var shouldRetryConnection = false
            while (true) {
                try {
                    httpClient.webSocket(
                        {
                            this.url(websocketUrl)
                        }
                    ) {
                        val incomingJob = incoming.consumeAsFlow()
                            .catch {
                                outgoing.close(it)
                            }
                            .onEach { frame ->
                                when (frame) {
                                    is Frame.Text -> {
                                        onMessage(frame.readText())
                                    }

                                    is Frame.Close -> {
                                        shouldRetryConnection =
                                            this@webSocket.closeReason.await()?.code != CloseReason.Codes.NORMAL.code
                                    }

                                    else -> {
                                        // just do nothing for now
                                    }
                                }

                            }
                            .launchIn(this)

                        val outgoingJob = outgoingMessageFlow
                            .onEach { outgoingMessage ->
                                when (outgoingMessage) {
                                    is OutgoingMessage.DataMessage -> {
                                        outgoing.send(Frame.Text(outgoingMessage.message))
                                    }

                                    is OutgoingMessage.CloseMessage -> {
                                        outgoing.send(
                                            Frame.Close(
                                                CloseReason(
                                                    outgoingMessage.closeCode,
                                                    outgoingMessage.message
                                                )
                                            )
                                        )
                                    }
                                }
                            }
                            .launchIn(this)
                        status = WebsocketStatus.Connected
                        joinAll(incomingJob, outgoingJob)
                    }
                } catch (e: Exception) {
                    shouldRetryConnection = true
                    // Log exception
                }
                status = WebsocketStatus.Reconnecting
                if (shouldRetryConnection && !retryPolicy.retryConnection()) {
                    break
                }
            }
            status = WebsocketStatus.Disconnected
        }
    }

    fun send(message: String) {
        coroutineScope.launch {
            outgoingMessageFlow.emit(OutgoingMessage.DataMessage(message))
        }
    }

    fun close(code: Short, message: String) {
        coroutineScope.launch {
            outgoingMessageFlow.emit(OutgoingMessage.CloseMessage(code, message))
        }
    }

    interface ConnectionStatusListener {
        operator fun invoke(status: WebsocketStatus)
    }

}