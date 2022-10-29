package com.alchemy.sdk.wallet.connect.v1

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import io.ktor.server.application.install
import io.ktor.server.cio.CIO
import io.ktor.server.engine.embeddedServer
import io.ktor.server.routing.routing
import io.ktor.server.websocket.WebSockets
import io.ktor.server.websocket.webSocket
import io.ktor.websocket.DefaultWebSocketSession
import io.ktor.websocket.Frame
import io.ktor.websocket.readText
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.withContext
import java.lang.ref.WeakReference
import java.util.Collections
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicInteger

class Connection(val session: DefaultWebSocketSession) {
    companion object {
        val lastId = AtomicInteger(0)
    }

    val name = "user${lastId.getAndIncrement()}"
}

internal class BridgeServer(
    gson: Gson
) {

    private val parameterizedType = TypeToken.getParameterized(
        Map::class.java,
        String::class.java,
        Any::class.java
    )

    private val pubs: MutableMap<String, MutableList<Connection>> = ConcurrentHashMap()
    private val pubsCache: MutableMap<String, String?> = ConcurrentHashMap()

    private val _state = MutableStateFlow<ConnectionState>(ConnectionState.Disconnected)
    val state: Flow<ConnectionState> = _state.asStateFlow()

    private val server = embeddedServer(CIO, 0) {
        install(WebSockets)
        routing {
            val connections = Collections.synchronizedSet<Connection>(LinkedHashSet())
            webSocket("/") {
                val thisConnection = Connection(this)
                connections += thisConnection
                try {
                    for (frame in incoming) {
                        frame as? Frame.Text ?: continue
                        val receivedText = frame.readText()
                        receivedText.also {
                            val msg = gson.fromJson(it, parameterizedType.type) as Map<String, Any?>? ?: error("Invalid message")
                            val type: String = msg["type"] as String? ?: error("Type not found")
                            val topic: String = msg["topic"] as String? ?: error("Topic not found")
                            when (type) {
                                "pub" -> {
                                    var sendMessage = false
                                    pubs[topic]?.forEach { r ->
                                        r.apply {
                                            session.send(Frame.Text(receivedText))
                                            sendMessage = true
                                        }
                                    }
                                    if (!sendMessage) {
                                        pubsCache[topic] = receivedText
                                    }
                                }
                                "sub" -> {
                                    pubs.getOrPut(topic) { mutableListOf() }
                                        .add(thisConnection)
                                    pubsCache[topic]?.let { cached ->
                                        connections.forEach {
                                            it.session.send(Frame.Text(cached))
                                        }
                                    }
                                }
                                else -> error("Unknown type")
                            }
                        }
                    }
                } finally {
                    connections -= thisConnection
                    pubs.forEach { (_, connections) -> connections.remove(thisConnection) }
                }
            }
        }
    }

    suspend fun start() = withContext(Dispatchers.IO) {
        server.start(wait = false)
        val port = server.resolvedConnectors().first().port
        _state.update {
            ConnectionState.Connected(port)
        }
    }

    fun stop() {
        pubs.clear()
        pubsCache.clear()
        server.stop()
    }

    sealed interface ConnectionState {
        object Disconnected : ConnectionState
        data class Connected(val port: Int) : ConnectionState
    }

}