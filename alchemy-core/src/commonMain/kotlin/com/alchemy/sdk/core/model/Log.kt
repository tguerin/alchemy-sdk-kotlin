package com.alchemy.sdk.core.model

import com.alchemy.sdk.util.HexString
import kotlinx.serialization.Serializable

@Serializable
data class Log(
    val address: Address,
    val topics: List<HexString>,
    val data: HexString,
    val blockNumber: HexString,
    val transactionHash: HexString,
    val transactionIndex: HexString,
    val blockHash: HexString,
    val logIndex: HexString,
    val removed: Boolean,
)
