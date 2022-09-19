package com.alchemy.sdk.core.model

@JvmInline
value class Percentile constructor(val value: Int) {

    companion object {
        val Int.percentile: Percentile
            get() {
                check(this in 0..100)
                return Percentile(this)
            }
    }
}