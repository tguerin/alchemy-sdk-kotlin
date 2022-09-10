package com.alchemy.sdk.json.rpc.client.model

data class JsonRpcRequest(
    val id: String,
    val jsonrpc: String = "2.0",
    val method: String,
    val params: List<Any?> = emptyList()
)