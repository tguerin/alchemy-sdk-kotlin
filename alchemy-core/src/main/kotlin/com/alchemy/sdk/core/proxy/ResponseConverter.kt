package com.alchemy.sdk.core.proxy

interface ResponseConverter<R : Any> {
    suspend fun convert(data: String): R
}