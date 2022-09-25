package com.alchemy.sdk.core.util

import okhttp3.Interceptor
import okhttp3.Response

object AlchemyVersionInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        if (chain.request().url.toString().contains("alchemy.com/nft")) {
            chain.request().newBuilder().addHeader(
                "Alchemy-Ethers-Sdk-Version", "2.0.3"
            )
        }
        return chain.proceed(chain.request())
    }
}