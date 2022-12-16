package com.alchemy.sdk.rpc.model

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonArray

@Serializable
data class JsonRpcRequest(
    val id: String,
    val jsonrpc: String = "2.0",
    val method: String,
    val params: JsonArray = JsonArray(emptyList())
)