package com.alchemy.sdk.core.model

import com.alchemy.sdk.core.util.Ether
import com.alchemy.sdk.core.util.HexString

data class BlockTransaction(
    val blockHash: HexString,
    val blockNumber: HexString,
    val hash: HexString,
    val accessList: List<HexString>, // TODO not sure about this type
    val chainId: HexString,
    val from: HexString,
    val gas: HexString,
    val gasPrice: Ether,
    val input: HexString,
    val maxFeePerGas: Ether,
    val maxPriorityFeePerGas: Ether,
    val nonce: HexString,
    val r: HexString,
    val s: HexString,
    val to: Address,
    val transactionIndex: HexString,
    val type: HexString,
    val v: HexString,
    val value: HexString,
)