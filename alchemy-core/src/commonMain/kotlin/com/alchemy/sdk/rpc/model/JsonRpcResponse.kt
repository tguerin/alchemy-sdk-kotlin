package com.alchemy.sdk.rpc.model

import kotlinx.serialization.Serializable

@Serializable
data class JsonRpcResponse<T>(
    val id: String,
    val jsonrpc: String,
    val result: T? = null,
    val error: JsonRpcError? = null
)