package com.alchemy.sdk.core.model

import com.alchemy.sdk.core.util.Ether
import com.alchemy.sdk.core.util.HexString

data class UncleBlock(
    val number: HexString,
    val difficulty: HexString,
    val extraData: HexString,
    val gasLimit: HexString,
    val gasUsed: HexString,
    val hash: HexString,
    val logsBloom: HexString,
    val miner: HexString,
    val mixHash: HexString,
    val nonce: HexString,
    val parentHash: HexString,
    val receiptsRoot: HexString,
    val sha3Uncles: HexString,
    val size: HexString,
    val stateRoot: HexString,
    val timestamp: HexString,
    val transactionsRoot: HexString,
    val uncles: List<HexString>,
    val baseFeePerGas: Ether,
)