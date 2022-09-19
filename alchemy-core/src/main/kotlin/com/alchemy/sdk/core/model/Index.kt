package com.alchemy.sdk.core.model

@JvmInline
value class Index constructor(val value: Int) {

    companion object {
        val Int.index: Index
            get() {
                check(this >= 0)
                return Index(this)
            }
    }
}