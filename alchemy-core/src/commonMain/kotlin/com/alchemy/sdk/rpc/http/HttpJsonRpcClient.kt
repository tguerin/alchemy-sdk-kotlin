package com.alchemy.sdk.rpc.http

import com.alchemy.sdk.rpc.model.JsonRpcException
import com.alchemy.sdk.rpc.model.JsonRpcRequest
import com.alchemy.sdk.rpc.model.JsonRpcResponse
import com.alchemy.sdk.util.SdkResult
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.isSuccess
import org.lighthousegames.logging.KmLog


suspend inline fun <reified T> HttpClient.call(url: String, request: JsonRpcRequest, logger: KmLog): SdkResult<T> {
    val response = post(url) {
        contentType(ContentType.Application.Json)
        setBody(request)
    }
    return if (response.status.isSuccess()) {
        logger.debug { response.bodyAsText() }
        val jsonRpcResponse: JsonRpcResponse<T> = response.body()
        if (jsonRpcResponse.result == null && jsonRpcResponse.error != null) {
            SdkResult.failure(JsonRpcException(jsonRpcResponse.error))
        } else {
            SdkResult.success(jsonRpcResponse.result as T)
        }
    } else {
        SdkResult.failure(RuntimeException("error.http.code.${response.status.value}: " + response.bodyAsText()))
    }
}
