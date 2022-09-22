package com.alchemy.sdk.core.model

import com.alchemy.sdk.core.util.HexString

sealed class LogFilter {
    data class BlockHashFilter(
        val blockHash: HexString,
        val addresses: List<Address> = emptyList(),
        val topics: HexString?= null
    ) : LogFilter()

    data class BlockRangeFilter(
        val from: BlockTag = BlockTag.Latest,
        val to: BlockTag = BlockTag.Latest,
        val addresses: List<Address> = emptyList(),
        val topics: HexString?= null
    ) : LogFilter()
}
