package com.alchemy.sdk.core.model

import com.alchemy.sdk.core.util.Ether
import com.alchemy.sdk.core.util.HexString

sealed class GasEstimation {
    data class CustomGasEstimation(
        val from: Address?,
        val to: Address,
        val gasPrice: Ether?,
        val value: String?,
        val data: HexString?
    ) : GasEstimation()

    data class BlockTagGasEstimation(val blockTag: BlockTag) : GasEstimation()
}

