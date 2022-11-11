package com.alchemy.sdk.rpc.model

data class JsonRpcError(
    val code: Int,
    val message: String,
    val data: String? = null
)