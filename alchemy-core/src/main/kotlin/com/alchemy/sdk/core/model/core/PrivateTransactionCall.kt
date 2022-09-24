package com.alchemy.sdk.core.model.core

import com.alchemy.sdk.core.util.HexString

data class PrivateTransactionCall(
    val tx: HexString,
    val maxBlockNumber: HexString? = null,
    val preferences: Preferences? = null,
) {
    class Preferences(val fast: Boolean)
}