package com.alchemy.sdk.core.model

import com.alchemy.sdk.core.adapter.KLogFilterSerializer
import com.alchemy.sdk.util.HexString
import kotlinx.serialization.Serializable

@Serializable(with = KLogFilterSerializer::class)
sealed interface LogFilter {
    @Serializable
    data class BlockHashFilter(
        val blockHash: HexString,
        val addresses: List<Address> = emptyList(),
        val topics: HexString? = null
    ) : LogFilter

    @Serializable
    data class BlockRangeFilter(
        val from: BlockTag = BlockTag.Latest,
        val to: BlockTag = BlockTag.Latest,
        val addresses: List<Address> = emptyList(),
        val topics: HexString? = null
    ) : LogFilter
}
