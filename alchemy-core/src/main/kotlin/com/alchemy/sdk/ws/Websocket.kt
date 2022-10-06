package com.alchemy.sdk.ws

import com.alchemy.sdk.json.rpc.client.generator.IdGenerator
import com.alchemy.sdk.json.rpc.client.model.JsonRpcException
import com.alchemy.sdk.json.rpc.client.model.JsonRpcRequest
import com.alchemy.sdk.json.rpc.client.model.JsonRpcResponse
import com.alchemy.sdk.ws.model.WebsocketEvent
import com.alchemy.sdk.ws.model.WebsocketMethod
import com.alchemy.sdk.ws.model.WebsocketStatus
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.google.gson.stream.JsonToken
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.map
import okhttp3.OkHttpClient
import java.io.StringReader

fun <T> Flow<WebsocketEvent<Result<T>>>.dataOnly(): Flow<Result<T>> {
    return this.filterIsInstance<WebsocketEvent.Data<Result<T>>>().map { it.data }
}

/**
 * Only for testing purpose
 */
internal fun <T> Flow<WebsocketEvent<Result<T>>>.statusOnly(): Flow<WebsocketStatus> {
    return this.filterIsInstance<WebsocketEvent.Status<Result<T>>>().map { it.status }
}

class Websocket internal constructor(
    private val idGenerator: IdGenerator,
    private val gson: Gson,
    websocketUrl: String,
    okHttpClientBuilder: OkHttpClient.Builder
) {

    private val websocketClient = WebsocketClient(websocketUrl, okHttpClientBuilder)

    fun <T> on(method: WebsocketMethod<T>): Flow<WebsocketEvent<Result<T>>> {
        return websocketClient.on(forgeEvent(method))
            .parseResponse(method.responseType)
    }

    fun <T> once(method: WebsocketMethod<T>): Flow<WebsocketEvent<Result<T>>> {
        return websocketClient
            .on(forgeEvent(method), 1)
            .parseResponse(method.responseType)

    }

    @Suppress("UNCHECKED_CAST")
    private fun <T> Flow<WebsocketEvent<String>>.parseResponse(responseType: Class<T>): Flow<WebsocketEvent<Result<T>>> {
        return this
            .map { event ->
                when (event) {
                    is WebsocketEvent.Data<String> -> {
                        val jsonReader = gson.newJsonReader(StringReader(event.data))
                        val typeToken = TypeToken.getParameterized(
                            JsonRpcResponse::class.java,
                            responseType
                        )
                        val adapter = gson.getAdapter(typeToken)
                        var dataRead: JsonRpcResponse<T>?
                        var exception: Throwable? = null
                        try {
                            dataRead = adapter.read(jsonReader) as JsonRpcResponse<T>
                            if (jsonReader.peek() !== JsonToken.END_DOCUMENT) {
                                dataRead = null
                                exception = RuntimeException("Json Parsing Failed")
                            }
                        } catch (e: Exception) {
                            dataRead = null
                            exception = e
                        }
                        val result = when {
                            exception != null -> {
                                Result.failure(exception)
                            }
                            dataRead != null -> {
                                val rpcError = dataRead.error
                                if (rpcError != null) {
                                    Result.failure(JsonRpcException(rpcError))
                                } else {
                                    Result.success(dataRead.result)
                                }
                            }
                            else -> {
                                // can't happen
                                Result.failure(RuntimeException("unknown"))
                            }
                        }
                        WebsocketEvent.Data(result)
                    }
                    is WebsocketEvent.Status<String> -> {
                        event
                    }
                } as WebsocketEvent<Result<T>>
            }
    }

    private fun <T> forgeEvent(method: WebsocketMethod<T>): String {
        return gson.toJson(
            JsonRpcRequest(
                id = idGenerator.generateId(),
                method = method.name,
                params = method.params
            )
        )
    }

}