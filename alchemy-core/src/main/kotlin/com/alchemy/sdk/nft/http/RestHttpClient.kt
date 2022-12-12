package com.alchemy.sdk.nft.http

import com.alchemy.sdk.util.GsonStringConverter
import com.alchemy.sdk.util.QueryMapObject
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.headers
import io.ktor.client.request.parameter
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.isSuccess


class RestHttpClient(
    val httpClient: HttpClient,
    val gsonStringConverter: GsonStringConverter
) {
    suspend inline fun <reified T> executeGet(
        url: String,
        headers: Map<String, String>,
        params: Map<String, Any?>
    ): Result<T> {
        val response = httpClient.get(url) {
            contentType(ContentType.Application.Json)
            headers {
                headers.forEach {
                    append(it.key, it.value)
                }
            }
            params.filter { it.value != null }.forEach { paramEntry ->
                if (paramEntry.value is QueryMapObject) {
                    (paramEntry.value as QueryMapObject).forEach { queryMapEntry ->
                        parameter(queryMapEntry.key, gsonStringConverter.convert(queryMapEntry.value))
                    }
                } else {
                    parameter(paramEntry.key, gsonStringConverter.convert(paramEntry.value))
                }
            }
        }
        return if (response.status.isSuccess()) {
            println("Response success ${response.bodyAsText()}")
            Result.success(response.body())
        } else {
            println("Response error " + "error.http.code.${response.status.value}: " + response.bodyAsText())
            Result.failure(RuntimeException("error.http.code.${response.status.value}: " + response.bodyAsText()))
        }
    }
}

