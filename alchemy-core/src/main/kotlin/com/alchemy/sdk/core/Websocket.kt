package com.alchemy.sdk.core

import com.alchemy.sdk.core.ws.model.WebsocketMethod
import com.alchemy.sdk.core.ws.WebsocketClient
import com.alchemy.sdk.json.rpc.client.generator.IdGenerator
import com.alchemy.sdk.json.rpc.client.model.JsonRpcException
import com.alchemy.sdk.json.rpc.client.model.JsonRpcRequest
import com.alchemy.sdk.json.rpc.client.model.JsonRpcResponse
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.google.gson.stream.JsonToken
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import okhttp3.OkHttpClient
import java.io.StringReader

class Websocket internal constructor(
    private val idGenerator: IdGenerator,
    private val gson: Gson,
    websocketUrl: String,
    okHttpClientBuilder: OkHttpClient.Builder
) {

    private val websocketClient = WebsocketClient(websocketUrl, okHttpClientBuilder)

    fun <T> on(method: WebsocketMethod<T>): Flow<Result<T>> {
        return websocketClient.on(forgeEvent(method))
            .parseResponse(method.responseType)
    }

    fun <T> once(method: WebsocketMethod<T>): Flow<Result<T>> {
        return websocketClient
            .on(forgeEvent(method), 1)
            .parseResponse(method.responseType)

    }

    @Suppress("UNCHECKED_CAST")
    private fun <T> Flow<String>.parseResponse(responseType: Class<T>) = this
        .map {
            val jsonReader = gson.newJsonReader(StringReader(it))
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
            if (exception != null) {
                Result.failure(exception)
            } else {
                Result.success(dataRead!!)
            }
        }
        .map {
            if (it.isFailure){
                Result.failure(it.exceptionOrNull()!!)
            } else {
                val response = it.getOrThrow()
                val rpcError = response.error
                if (rpcError != null) {
                    Result.failure(JsonRpcException(rpcError))
                } else {
                    Result.success(response.result)
                }
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