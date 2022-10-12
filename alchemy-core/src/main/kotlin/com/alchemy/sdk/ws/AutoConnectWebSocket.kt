package com.alchemy.sdk.ws

import com.alchemy.sdk.ws.model.WebsocketStatus
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import okio.ByteString
import java.util.Timer
import java.util.TimerTask
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicInteger

/**
 * Adapted from https://github.com/VinsonGuo/ReconnectWebSocketWrapper
 */
internal class AutoConnectWebSocket(
    private val okHttpClient: OkHttpClient,
    private val request: Request,
    listener: WebSocketListener,
    private val onConnectStatusChangeListener: ConnectionStatusListener,
    private val config: Config = Config(),
) : WebSocket {

    data class Config(val reconnectInterval: Long = TimeUnit.SECONDS.toMillis(5))

    private val isConnected = AtomicBoolean(false)

    private val isConnecting = AtomicBoolean(false)

    private val isStopped = AtomicBoolean(false)

    /**
     * get the status of reconnection
     */
    val status: WebsocketStatus
        get() = when {
            isConnected.get() -> WebsocketStatus.Connected
            isConnecting.get() -> WebsocketStatus.Reconnecting
            else -> WebsocketStatus.Disconnected
        }

    /**
     * this listener will be invoked before on reconnection
     *
     * if you want to modify request when reconnection, you can set this listener
     */
    var onPreReconnectListener: ((request: Request) -> Request) = { request -> request }

    /**
     * the count of attempt to reconnect
     */
    val reconnectAttemptCount = AtomicInteger(0)

    private var timer: Timer? = null

    private val webSocketListener = object : WebSocketListener() {
        override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
            isConnected.compareAndSet(true, false)
            onConnectStatusChangeListener(webSocket, status)
            listener.onClosed(webSocket, code, reason)
            if (code != 1000) {
                doReconnect()
            } else {
                isStopped.compareAndSet(false, true)
            }
        }

        override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
            listener.onClosing(webSocket, code, reason)
        }

        override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
            isConnected.compareAndSet(true, false)
            onConnectStatusChangeListener(webSocket, status)
            doReconnect()
            listener.onFailure(webSocket, t, response)
        }

        override fun onMessage(webSocket: WebSocket, text: String) {
            listener.onMessage(webSocket, text)
        }

        override fun onMessage(webSocket: WebSocket, bytes: ByteString) {
            listener.onMessage(webSocket, bytes)
        }

        override fun onOpen(webSocket: WebSocket, response: Response) {
            isConnected.compareAndSet(false, true)
            isConnecting.compareAndSet(true, false)
            isStopped.compareAndSet(true, false)

            onConnectStatusChangeListener(webSocket, status)

            synchronized(this) {
                timer?.cancel()
                timer = null
            }
            reconnectAttemptCount.set(0)
            listener.onOpen(webSocket, response)
        }
    }

    private var webSocket: WebSocket

    init {
        webSocket = okHttpClient.newWebSocket(request, webSocketListener)
        onConnectStatusChangeListener(webSocket, status)
    }

    private fun doReconnect() {
        if (isConnected.get() || isConnecting.get()) {
            return
        }
        isStopped.compareAndSet(true, false)
        isConnecting.compareAndSet(false, true)

        onConnectStatusChangeListener(webSocket, status)

        synchronized(this) {
            timer = Timer().also {
                it.scheduleAtFixedRate(
                    object : TimerTask() {
                        override fun run() {
                            webSocket.cancel()
                            val reconnectRequest = onPreReconnectListener(request)
                            webSocket =
                                okHttpClient.newWebSocket(reconnectRequest, webSocketListener)
                        }
                    },
                    0,
                    config.reconnectInterval
                )
            }
        }
    }

    override fun cancel() {
        isConnected.compareAndSet(true, false)
        onConnectStatusChangeListener(webSocket, status)
        timer?.cancel()
        timer = null
        webSocket.cancel()
    }

    override fun close(code: Int, reason: String?): Boolean {
        onConnectStatusChangeListener(webSocket, WebsocketStatus.Disconnected)
        return webSocket.close(code, reason)
    }

    override fun queueSize(): Long {
        return webSocket.queueSize()
    }

    override fun request(): Request {
        return webSocket.request()
    }

    override fun send(text: String): Boolean {
        return webSocket.send(text)
    }

    override fun send(bytes: ByteString): Boolean {
        return webSocket.send(bytes)
    }

    fun emit(message: String) {
        webSocketListener.onMessage(webSocket, message)
    }

    fun connect() {
        if (isStopped.get()) {
            doReconnect()
        }
    }

    interface ConnectionStatusListener {
        operator fun invoke(webSocket: WebSocket, status: WebsocketStatus)
    }

}