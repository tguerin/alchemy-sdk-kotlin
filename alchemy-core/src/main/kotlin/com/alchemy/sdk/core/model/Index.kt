package com.alchemy.sdk.core.model

import com.alchemy.sdk.core.adapter.KIndexSerializer
import kotlinx.serialization.Serializable

@JvmInline
@Serializable(with = KIndexSerializer::class)
value class Index constructor(val value: Int) {

    companion object {
        val Int.index: Index
            get() {
                check(this >= 0)
                return Index(this)
            }
    }
}