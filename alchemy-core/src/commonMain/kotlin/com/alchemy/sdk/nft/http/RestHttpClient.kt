package com.alchemy.sdk.nft.http

import com.alchemy.sdk.util.SdkResult
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.headers
import io.ktor.client.request.parameter
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.isSuccess
import org.lighthousegames.logging.KmLog


suspend inline fun <reified T> HttpClient.executeGet(
    url: String,
    headers: Map<String, String>,
    params: Map<String, String?>,
    logger: KmLog,
): SdkResult<T> {
    val response = try {
        get(url) {
            contentType(ContentType.Application.Json)
            headers {
                headers.forEach {
                    append(it.key, it.value)
                }
            }
            params.filter { it.value != null }.forEach { paramEntry ->
                val paramValue = paramEntry.value
                check(paramValue != null)
                val sanitizedValue = paramValue.replace("\"", "")
                if (sanitizedValue.startsWith("[")) {
                    sanitizedValue
                        .replace("[", "")
                        .replace("]", "")
                        .split(",")
                        .forEach {
                            parameter(paramEntry.key, it)
                        }
                } else {
                    parameter(paramEntry.key, sanitizedValue)
                }
            }
        }
    } catch (e: Exception) {
        error(e)
    }
    return if (response.status.isSuccess()) {
        logger.debug { response.bodyAsText() }
        SdkResult.success(response.body())
    } else {
        SdkResult.failure(RuntimeException("error.http.code.${response.status.value}: " + response.bodyAsText()))
    }
}

