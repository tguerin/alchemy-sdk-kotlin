package com.alchemy.sdk.ws.model

import com.alchemy.sdk.core.model.Address
import com.alchemy.sdk.util.Ether
import com.alchemy.sdk.util.HexString

sealed interface PendingTransaction {
    val hash: HexString

    data class HashOnly(override val hash: HexString) : PendingTransaction

    data class FullPendingTransaction(
        val blockHash: HexString?,
        val blockNumber: HexString?,
        val from: Address,
        val gas: HexString,
        val gasPrice: Ether,
        override val hash: HexString,
        val input: HexString,
        val nonce: HexString,
        val to: HexString,
        val transactionIndex: HexString?,
        val value: HexString,
        val type: HexString,
        val chainId: HexString,
        val v: HexString,
        val r: HexString,
        val s: HexString,
    ) : PendingTransaction
}