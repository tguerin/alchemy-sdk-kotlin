package com.alchemy.sdk.core.model

import com.alchemy.sdk.core.adapter.KBlockCountSerializer
import kotlinx.serialization.Serializable
import kotlin.jvm.JvmInline

@JvmInline
@Serializable(with = KBlockCountSerializer::class)
value class BlockCount constructor(val value: Int) {

    companion object {
        val Int.blockCount: BlockCount
            get() {
                check(this > 0)
                return BlockCount(this)
            }
    }
}