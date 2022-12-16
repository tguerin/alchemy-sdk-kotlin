package com.alchemy.sdk.core.model

import com.alchemy.sdk.util.HexString
import kotlinx.serialization.Serializable

@Serializable
data class PrivateTransactionCall(
    val tx: HexString,
    val maxBlockNumber: HexString? = null,
    val preferences: Preferences? = null,
) {
    @Serializable
    class Preferences(val fast: Boolean)
}