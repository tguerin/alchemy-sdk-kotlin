package com.alchemy.sdk.core.model

import com.alchemy.sdk.core.util.HexString
import com.alchemy.sdk.core.util.Wei

data class TransactionReceipt(
    val transactionHash: HexString,
    val blockHash: HexString,
    val blockNumber: HexString,
    val contractAddress: Address?,
    val cumulativeGasUsed: HexString,
    val effectiveGasPrice: Wei,
    val from: Address,
    val gasUsed: HexString,
    val logs: List<HexString>,
    val logsBloom: HexString,
    val status: HexString,
    val to: Address,
    val transactionIndex: HexString,
    val type: HexString
)