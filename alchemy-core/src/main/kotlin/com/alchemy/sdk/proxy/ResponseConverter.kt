package com.alchemy.sdk.proxy

interface ResponseConverter<R : Any> {
    suspend fun convert(data: String): R
}