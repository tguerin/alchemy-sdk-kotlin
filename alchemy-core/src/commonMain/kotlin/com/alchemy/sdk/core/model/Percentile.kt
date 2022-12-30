package com.alchemy.sdk.core.model

import com.alchemy.sdk.core.adapter.KPercentileSerializer
import kotlinx.serialization.Serializable
import kotlin.jvm.JvmInline

@JvmInline
@Serializable(with = KPercentileSerializer::class)
value class Percentile constructor(val value: Float) {

    companion object {
        val Int.percentile: Percentile
            get() {
                return this.toFloat().percentile
            }

        val Float.percentile: Percentile
            get() {
                check(this in 0.0..100.0)
                return Percentile(this)
            }
    }
}