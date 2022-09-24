package com.alchemy.sdk.core.model.core


data class RawInt constructor(val value: Int) {

    companion object {
        val Int.raw: RawInt
            get() {
                return RawInt(this)
            }
    }
}