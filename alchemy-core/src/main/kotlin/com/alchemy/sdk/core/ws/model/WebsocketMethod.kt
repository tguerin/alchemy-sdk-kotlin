package com.alchemy.sdk.core.ws.model

import com.alchemy.sdk.core.util.HexString

sealed class WebsocketMethod<R>(
    val name: String,
    val responseType: Class<R>,
    val params: List<Any?> = emptyList()
) {
    object Block : WebsocketMethod<HexString>("eth_blockNumber", HexString::class.java)
}