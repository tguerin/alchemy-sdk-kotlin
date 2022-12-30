package com.alchemy.sdk.ws.model

import com.alchemy.sdk.core.model.Address
import com.alchemy.sdk.util.Ether
import com.alchemy.sdk.util.HexString
import com.alchemy.sdk.ws.adapter.KHashOnlySerializer
import com.alchemy.sdk.ws.adapter.KPendingTransactionSerializer
import kotlinx.serialization.Serializable

@Serializable(with = KPendingTransactionSerializer::class)
sealed interface PendingTransaction {
    val hash: HexString

    @Serializable(with = KHashOnlySerializer::class)
    data class HashOnly(override val hash: HexString) : PendingTransaction

    @Serializable
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