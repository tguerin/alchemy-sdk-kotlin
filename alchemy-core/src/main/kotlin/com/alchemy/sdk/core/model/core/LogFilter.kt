package com.alchemy.sdk.core.model.core

import com.alchemy.sdk.core.util.HexString

sealed interface LogFilter {
    data class BlockHashFilter(
        val blockHash: HexString,
        val addresses: List<Address> = emptyList(),
        val topics: HexString? = null
    ) : LogFilter

    data class BlockRangeFilter(
        val from: BlockTag = BlockTag.Latest,
        val to: BlockTag = BlockTag.Latest,
        val addresses: List<Address> = emptyList(),
        val topics: HexString? = null
    ) : LogFilter
}
