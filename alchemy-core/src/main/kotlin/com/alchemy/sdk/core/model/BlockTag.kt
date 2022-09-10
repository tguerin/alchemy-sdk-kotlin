package com.alchemy.sdk.core.model

import com.alchemy.sdk.core.util.HexString

sealed class BlockTag(val value: String) {
    object Latest : BlockTag("latest")
    object Pending : BlockTag("pending")
    object Safe : BlockTag("safe")
    object Finalized : BlockTag("finalized")
    object Earliest : BlockTag("earlies")
    class BlockTagNumber(blockTagNumber: HexString) : BlockTag(blockTagNumber.toString())
}