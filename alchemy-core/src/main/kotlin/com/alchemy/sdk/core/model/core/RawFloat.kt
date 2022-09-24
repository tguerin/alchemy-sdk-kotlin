package com.alchemy.sdk.core.model.core


data class RawFloat constructor(val value: Float) {

    companion object {
        val Float.raw: RawFloat
            get() {
                return RawFloat(this)
            }
    }
}