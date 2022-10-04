package com.alchemy.sdk.util

import okhttp3.Interceptor
import okhttp3.Response

object AlchemyVersionInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        return if (chain.request().url.toString().contains("alchemy.com/nft")) {
            val request = chain.request()
                .newBuilder()
                .addHeader(
                    "Alchemy-Ethers-Sdk-Version", "2.0.3"
                )
                .build()
            chain.proceed(request)
        } else {
            chain.proceed(chain.request())
        }
    }
}