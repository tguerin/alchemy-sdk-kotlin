package com.alchemy.sdk.core.model

@JvmInline
value class BlockCount constructor(val value: Int) {

    companion object {
        val Int.blockCount: BlockCount
            get() {
                check(this > 0)
                return BlockCount(this)
            }
    }
}