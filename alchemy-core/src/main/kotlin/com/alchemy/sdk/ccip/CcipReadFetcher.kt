package com.alchemy.sdk.ccip

import com.alchemy.sdk.core.model.CcipReadResponse
import com.alchemy.sdk.core.model.TransactionCall
import com.alchemy.sdk.util.HexString
import com.google.gson.Gson
import kotlinx.coroutines.suspendCancellableCoroutine
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import java.io.IOException

class CcipReadFetcher internal constructor(
    private val okHttpClient: OkHttpClient,
    private val gson: Gson
) {
    suspend fun fetchCcipRead(
        transactionCall: TransactionCall,
        calldata: HexString?,
        urls: List<String>
    ): HexString? {
        if (urls.isEmpty()) return null
        val sender = transactionCall.to
        for (url in urls) {
            val href = url
                .replace("{sender}", sender.value.data)
                .replace("{data}", calldata?.data ?: "")
            val json = if (url.indexOf("{data}") >= 0) {
                null
            } else {
                "{\"data\":\"${calldata?.data}\",\"sender\":\"${sender.value.data}\"}"
            }
            val result = suspendCancellableCoroutine { continuation ->
                okHttpClient.newCall(
                    Request.Builder()
                        .url(href)
                        .apply {
                            if (json == null) {
                                get()
                            } else {
                                post(json.toRequestBody("application/json".toMediaType()))
                            }
                        }
                        .build()
                ).enqueue(
                    object : Callback {
                        override fun onFailure(call: Call, e: IOException) {
                            continuation.resume(Result.failure(e), onCancellation = null)
                        }

                        override fun onResponse(call: Call, response: Response) {
                            when {
                                response.isSuccessful -> {
                                    val body = response.body?.string() ?: ""
                                    if (body.isEmpty()) {
                                        continuation.resume(
                                            Result.success(null), onCancellation = null
                                        )
                                    } else {
                                        continuation.resume(
                                            Result.success(
                                                gson.fromJson(
                                                    body,
                                                    CcipReadResponse::class.java
                                                ).data
                                            ), onCancellation = null
                                        )
                                    }
                                }
                                response.code in 400..499 -> {
                                    continuation.resume(
                                        Result.success(null), onCancellation = null
                                    )
                                }
                                else -> {
                                    continuation.resume(
                                        Result.failure(IOException("error.http.${response.code}")),
                                        onCancellation = null
                                    )
                                }
                            }
                        }
                    }
                )
            }
            return if (result.isSuccess) {
                result.getOrThrow()
            } else {
                continue
            }
        }
        return null
    }
}