package com.alchemy.sdk.core.model

import com.alchemy.sdk.core.adapter.KBlockTagSerializer
import com.alchemy.sdk.util.HexString
import com.alchemy.sdk.util.HexString.Companion.hexString
import kotlinx.serialization.Serializable

@Serializable(with = KBlockTagSerializer::class)
sealed class BlockTag(val value: String) {
    object Latest : BlockTag("latest")
    object Pending : BlockTag("pending")
    object Safe : BlockTag("safe")
    object Finalized : BlockTag("finalized")
    object Earliest : BlockTag("earliest")
    class BlockTagNumber(blockTagNumber: HexString) : BlockTag(blockTagNumber.data) {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false
            return true
        }

        override fun hashCode(): Int {
            return javaClass.hashCode()
        }
    }

    val String.blockTag: BlockTag
        get() {
            return when (this) {
                "latest" -> Latest
                "pending" -> Pending
                "safe" -> Safe
                "finalized" -> Finalized
                "earliest" -> Earliest
                else -> BlockTagNumber(this.hexString)
            }
        }

    val HexString.blockTag: BlockTag
        get() {
            return BlockTagNumber(this)
        }
}