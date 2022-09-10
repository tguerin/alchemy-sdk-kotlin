package com.alchemy.sdk.core.model

import androidx.annotation.IntRange

class StoragePosition private constructor(val position: Int) {
    companion object {
        fun from(@IntRange(from = 0) position: Int): StoragePosition {
            check(position >= 0)
            return StoragePosition(position)
        }
    }
}