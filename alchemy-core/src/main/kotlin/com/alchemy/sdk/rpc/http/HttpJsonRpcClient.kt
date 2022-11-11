package com.alchemy.sdk.rpc.http

import com.alchemy.sdk.rpc.model.JsonRpcException
import com.alchemy.sdk.rpc.model.JsonRpcRequest
import com.alchemy.sdk.rpc.model.JsonRpcResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.isSuccess


suspend inline fun <reified T> HttpClient.call(url: String, request: JsonRpcRequest): Result<T> {
    val response = this.post(url) {
        contentType(ContentType.Application.Json)
        setBody(request)
    }
    return if (response.status.isSuccess()) {
        val jsonRpcResponse: JsonRpcResponse<T> = response.body()
        if (jsonRpcResponse.result == null && jsonRpcResponse.error != null) {
            Result.failure(JsonRpcException(jsonRpcResponse.error))
        } else {
            Result.success(jsonRpcResponse.result)
        }
    } else {
        Result.failure(RuntimeException("error.http.code.${response.status.value}: " + response.bodyAsText()))
    }
}
