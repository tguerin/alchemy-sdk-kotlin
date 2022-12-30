package com.alchemy.sdk.rpc.model

import kotlinx.serialization.Serializable

@Serializable
data class JsonRpcError(
    val code: Int,
    val message: String,
    val data: String? = null
)