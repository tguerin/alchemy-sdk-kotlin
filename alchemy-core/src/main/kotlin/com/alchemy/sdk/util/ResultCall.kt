package com.alchemy.sdk.util

import okhttp3.Request
import okio.Timeout
import retrofit2.Call
import retrofit2.Callback
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException

internal class ResultCall<T>(private val delegate: Call<T>) : Call<Result<T>> {

    override fun enqueue(callback: Callback<Result<T>>) {
        delegate.enqueue(ResultCallback(this, callback))
    }

    override fun isExecuted(): Boolean {
        return delegate.isExecuted
    }

    override fun execute(): Response<Result<T>> {
        return Response.success(Result.success(delegate.execute().body()!!))
    }

    override fun cancel() {
        delegate.cancel()
    }

    override fun isCanceled(): Boolean {
        return delegate.isCanceled
    }

    override fun clone(): Call<Result<T>> {
        return ResultCall(delegate.clone())
    }

    override fun request(): Request {
        return delegate.request()
    }

    override fun timeout(): Timeout {
        return delegate.timeout()
    }

    class ResultCallback<T>(
        private val resultCall: ResultCall<T>,
        private val callback: Callback<Result<T>>
    ) : Callback<T> {
        override fun onResponse(call: Call<T>, response: Response<T>) {
            if (response.isSuccessful) {
                callback.onResponse(
                    resultCall,
                    Response.success(
                        response.code(),
                        Result.success(response.body()!!)
                    )
                )
            } else {
                callback.onResponse(
                    resultCall,
                    Response.success(
                        Result.failure(
                            HttpException(response)
                        )
                    )
                )
            }
        }

        override fun onFailure(call: Call<T>, t: Throwable) {
            val errorMessage = when (t) {
                is IOException -> "No internet connection"
                else -> t.localizedMessage
            }
            callback.onResponse(
                resultCall,
                Response.success(Result.failure(RuntimeException(errorMessage, t)))
            )
        }
    }
}
