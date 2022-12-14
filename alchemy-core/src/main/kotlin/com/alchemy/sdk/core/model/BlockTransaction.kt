package com.alchemy.sdk.core.model

import com.alchemy.sdk.core.adapter.KBlockTransactionSerializer
import com.alchemy.sdk.core.adapter.KSimpleBlockTransactionSerializer
import com.alchemy.sdk.util.Ether
import com.alchemy.sdk.util.HexString
import kotlinx.serialization.Serializable

@Serializable(with = KBlockTransactionSerializer::class)
sealed interface BlockTransaction {
    @Serializable(with = KSimpleBlockTransactionSerializer::class)
    data class SimpleBlockTransaction(val hash: HexString) : BlockTransaction

    @Serializable
    data class FullBlockTransaction(
        val blockHash: HexString,
        val blockNumber: HexString,
        val hash: HexString,
        val accessList: List<HexString> = emptyList(), // TODO not sure about this type
        val chainId: HexString,
        val from: HexString,
        val gas: HexString,
        val gasPrice: Ether,
        val input: HexString,
        val maxFeePerGas: Ether? = null,
        val maxPriorityFeePerGas: Ether? = null,
        val nonce: HexString,
        val r: HexString,
        val s: HexString,
        val to: Address,
        val transactionIndex: HexString,
        val type: HexString,
        val v: HexString,
        val value: HexString,
    ) : BlockTransaction
}
