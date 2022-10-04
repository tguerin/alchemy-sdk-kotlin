package com.alchemy.sdk.ws

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
class AutoConnectWebsocket(
    private val okHttpClient: OkHttpClient,
    private val request: Request,
    listener: WebSocketListener,
    private val config: Config = Config(),
) : WebSocket {

    data class Config(val reconnectInterval: Long = TimeUnit.SECONDS.toMillis(1))

    enum class Status {
        Connected,
        Connecting,
        Disconnected
    }

    private val isConnected = AtomicBoolean(false)

    private val isConnecting = AtomicBoolean(false)

    /**
     * get the status of reconnection
     */
    val status: Status
        get() = when {
            isConnected.get() -> Status.Connected
            isConnecting.get() -> Status.Connecting
            else -> Status.Disconnected
        }

    /**
     * if you want to listen the reconnection status change, you can set this listener
     */
    var onConnectStatusChangeListener: ((status: Status) -> Unit) = { }

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

    private val timer: Timer by lazy { Timer() }

    private val webSocketListener = object : WebSocketListener() {
        override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
            isConnected.compareAndSet(true, false)
            onConnectStatusChangeListener.invoke(status)
            doReconnect()
            listener.onClosed(webSocket, code, reason)
        }

        override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
            listener.onClosing(webSocket, code, reason)
        }

        override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
            isConnected.compareAndSet(true, false)
            onConnectStatusChangeListener.invoke(status)
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

            onConnectStatusChangeListener.invoke(status)

            synchronized(this) {
                timer.cancel()
            }
            reconnectAttemptCount.set(0)
            listener.onOpen(webSocket, response)
        }
    }

    private var webSocket: WebSocket

    init {
        onConnectStatusChangeListener.invoke(status)
        webSocket = okHttpClient.newWebSocket(request, webSocketListener)
    }

    private fun doReconnect() {
        if (isConnected.get() || isConnecting.get()) {
            return
        }
        isConnecting.compareAndSet(false, true)

        onConnectStatusChangeListener.invoke(status)

        synchronized(this) {
            timer.scheduleAtFixedRate(
                object : TimerTask() {
                    override fun run() {
                        webSocket.cancel()
                        val reconnectRequest = onPreReconnectListener.invoke(request)
                        webSocket = okHttpClient.newWebSocket(reconnectRequest, webSocketListener)
                    }
                },
                0,
                config.reconnectInterval
            )
        }
    }

    override fun cancel() {
        isConnected.compareAndSet(true, false)
        onConnectStatusChangeListener.invoke(status)
        timer.cancel()
        webSocket.cancel()
    }

    override fun close(code: Int, reason: String?): Boolean {
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

}