package com.alchemy.sdk.ws.model

import com.alchemy.sdk.rpc.model.JsonRpcError
import com.alchemy.sdk.util.HexString

data class WebSocketJsonRpcResponse<T>(
    val jsonrpc: String,
    val method: String,
    val params: JsonRpcResult<T>,
    val subscription: HexString,
)

data class JsonRpcResult<T>(
    val result: T?,
    val error: JsonRpcError?
)