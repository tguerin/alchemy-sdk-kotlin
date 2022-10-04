package com.alchemy.sdk.proxy


interface ParameterConverter<in R : Any, out T : Any> {
    suspend fun convert(data: R): T
}