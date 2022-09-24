package com.alchemy.sdk.core.model.core

import com.alchemy.sdk.core.util.HexString

data class CancelPrivateTransactionRequest(
    val txHash: HexString
)