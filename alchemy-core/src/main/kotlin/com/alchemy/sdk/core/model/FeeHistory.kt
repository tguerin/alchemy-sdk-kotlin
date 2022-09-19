package com.alchemy.sdk.core.model

import com.alchemy.sdk.core.util.HexString

typealias RewardList = List<List<HexString>>

data class FeeHistory(
    val oldestBlock: HexString,
    val reward: RewardList?,
    val baseFeePerGas: List<HexString>,
    val gasUsedRatio: List<Double>
)