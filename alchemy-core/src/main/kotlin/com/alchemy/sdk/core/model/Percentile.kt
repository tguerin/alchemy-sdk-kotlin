package com.alchemy.sdk.core.model

@JvmInline
value class Percentile constructor(val value: RawFloat) {

    companion object {
        val Int.percentile: Percentile
            get() {
                return this.toFloat().percentile
            }

        val Float.percentile: Percentile
            get() {
                check(this in 0.0..100.0)
                return Percentile(RawFloat(this))
            }
    }
}