package com.alchemy.sdk.json.rpc.client.http

import com.alchemy.sdk.json.rpc.client.JsonRpcClient
import com.alchemy.sdk.json.rpc.client.model.JsonRpcException
import com.alchemy.sdk.json.rpc.client.model.JsonRpcRequest
import com.alchemy.sdk.json.rpc.client.model.JsonRpcResponse
import com.alchemy.sdk.json.rpc.client.util.parseContent
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
import java.lang.reflect.Type


class HttpJsonRpcClient(
    private val url: String,
    private val okHttpClient: OkHttpClient,
    private val gson: Gson
) : JsonRpcClient {

    override suspend fun <T> call(request: JsonRpcRequest, returnType: Type): Result<T> =
        suspendCancellableCoroutine { continuation ->
            okHttpClient.newCall(
                Request.Builder()
                    .url(url)
                    .post(gson.toJson(request).toRequestBody(JSON.toMediaType()))
                    .build()
            ).enqueue(
                object : Callback {
                    override fun onFailure(call: Call, e: IOException) {
                        continuation.resume(Result.failure(e), onCancellation = null)
                    }

                    @Suppress("UNCHECKED_CAST")
                    override fun onResponse(call: Call, response: Response) {
                        // TODO better error handling
                        val data = response.body?.use {
                            val (dataRead, _) = gson.parseContent<JsonRpcResponse<T>>(
                                JsonRpcResponse::class.java,
                                returnType,
                                it.charStream()
                            )
                            dataRead
                        }
                        if (response.isSuccessful) {
                            if (data != null) {
                                if (data.result == null && data.error != null) {
                                    continuation.resume(
                                        Result.failure(JsonRpcException(data.error)),
                                        onCancellation = null
                                    )
                                } else {
                                    continuation.resume(
                                        Result.success(data.result),
                                        onCancellation = null
                                    )
                                }
                            } else {
                                continuation.resume(
                                    Result.failure(RuntimeException("error.invalid.body")),
                                    onCancellation = null
                                )
                            }
                        } else {
                            continuation.resume(
                                Result.failure(RuntimeException("error.http.code.${response.code}: " + data?.error?.message)),
                                onCancellation = null
                            )
                        }
                    }
                }
            )
        }

    companion object {
        const val JSON = "application/json; charset=UTF-8"
    }
}