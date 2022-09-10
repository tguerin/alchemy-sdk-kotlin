package com.alchemy.sdk.json.rpc.client.model

data class JsonRpcResponse<T>(
    val id: String,
    val result: T,
    val error: JsonRpcError?
)