package com.alchemy.sdk.ws.model

import com.alchemy.sdk.rpc.model.JsonRpcError
import kotlinx.serialization.Serializable

@Serializable
data class WebSocketJsonRpcResponse<T>(
    val jsonrpc: String,
    val method: String,
    val params: JsonRpcResult<T>
)

@Serializable
data class JsonRpcResult<T>(
    val result: T? = null,
    val error: JsonRpcError? = null
)