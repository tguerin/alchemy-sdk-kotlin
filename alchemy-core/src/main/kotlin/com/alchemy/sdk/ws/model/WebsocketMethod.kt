package com.alchemy.sdk.ws.model

import com.alchemy.sdk.core.model.Address
import com.alchemy.sdk.util.HexString

sealed class WebsocketMethod<R>(
    val params: List<Any?>,
    val responseType: Class<R>,
    val name: String = "eth_subscribe",
) {

    object Block : WebsocketMethod<BlockHead>(
        listOf("newHeads"),
        BlockHead::class.java
    )

    data class PendingTransactions(
        val fromAddress: Address? = null,
        val toAddresses: List<Address> = emptyList(),
        val hashesOnly: Boolean = false,
    ) : WebsocketMethod<PendingTransaction>(
        mutableListOf("alchemy_pendingTransactions", PendingTransactionCall(hashesOnly)),
        PendingTransaction::class.java
    )

    data class UnSubscribe(
        private val subscriptionId: HexString
    ) : WebsocketMethod<Boolean>(
        listOf(subscriptionId),
        Boolean::class.java,
        "eth_unsubscribe"
    )
}