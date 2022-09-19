package com.alchemy.sdk.core.model

import com.alchemy.sdk.core.util.HexString
import com.alchemy.sdk.core.util.Wei

data class BlockTransaction(
    val blockHash: HexString,
    val blockNumber: HexString,
    val hash: HexString,
    val accessList: List<HexString>, // TODO not sure about this type
    val chainId: HexString,
    val from: HexString,
    val gas: HexString,
    val gasPrice: Wei,
    val input: HexString,
    val maxFeePerGas: Wei,
    val maxPriorityFeePerGas: Wei,
    val nonce: HexString,
    val r: HexString,
    val s: HexString,
    val to: Address,
    val transactionIndex: HexString,
    val type: HexString,
    val v: HexString,
    val value: HexString,
)