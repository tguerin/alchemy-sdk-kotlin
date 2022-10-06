package com.alchemy.sdk.ws.model

import com.alchemy.sdk.util.Ether
import com.alchemy.sdk.util.HexString

data class BlockHead(
    val baseFeePerGas: Ether,
    val difficulty: HexString,
    val extraData: HexString,
    val gasLimit: HexString,
    val gasUsed: HexString,
    val hash: HexString,
    val logsBloom: HexString,
    val miner: HexString,
    val mixHash: HexString,
    val nonce: HexString,
    val number: HexString,
    val parentHash: HexString,
    val receiptsRoot: HexString,
    val sha3Uncles: HexString,
    val size: HexString,
    val stateRoot: HexString,
    val timestamp: HexString,
    val transactionsRoot: HexString
)