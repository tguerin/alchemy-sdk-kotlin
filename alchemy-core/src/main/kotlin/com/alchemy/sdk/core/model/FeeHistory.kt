package com.alchemy.sdk.core.model

import com.alchemy.sdk.util.HexString
import kotlinx.serialization.Serializable

typealias RewardList = List<List<HexString>>

@Serializable
data class FeeHistory(
    val oldestBlock: HexString,
    val reward: RewardList? = emptyList(),
    val baseFeePerGas: List<HexString>,
    val gasUsedRatio: List<Double>
)