package com.alchemy.sdk.json.rpc.client.model

data class JsonRpcError(
    val code: Int,
    val message: String,
    val data: String? = null
)