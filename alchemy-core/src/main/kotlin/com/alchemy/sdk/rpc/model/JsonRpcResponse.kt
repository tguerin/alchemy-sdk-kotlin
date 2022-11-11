package com.alchemy.sdk.rpc.model

data class JsonRpcResponse<T>(
    val id: String,
    val result: T,
    val error: JsonRpcError?
)