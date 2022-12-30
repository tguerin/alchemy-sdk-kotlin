package com.alchemy.sdk.ccip

import com.alchemy.sdk.core.model.CcipReadResponse
import com.alchemy.sdk.core.model.TransactionCall
import com.alchemy.sdk.util.HexString
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.content.TextContent
import io.ktor.http.isSuccess

class CcipReadFetcher internal constructor(
    private val httpClient: HttpClient
) {
    suspend fun fetchCcipRead(
        transactionCall: TransactionCall,
        callData: HexString?,
        urls: List<String>
    ): HexString? {
        if (urls.isEmpty()) return null
        val sender = transactionCall.to
        for (url in urls) {
            val href = url
                .replace("{sender}", sender.value.data)
                .replace("{data}", callData?.data ?: "")
            val json = if (url.indexOf("{data}") >= 0) {
                null
            } else {
                "{\"data\":\"${callData?.data}\",\"sender\":\"${sender.value.data}\"}"
            }

            val response = if (json == null) {
                httpClient.get(href)
            } else {
                httpClient.post(href) {
                    setBody(TextContent(json, ContentType.Application.Json))
                }
            }
            when {
                response.status.isSuccess() -> {
                    return try {
                        response.body<CcipReadResponse>().data
                    } catch (e: Exception) {
                        null
                    }
                }

                response.status.value in 400..499 -> {
                    continue
                }

                else -> {
                    continue
                }
            }
        }
        return null
    }
}