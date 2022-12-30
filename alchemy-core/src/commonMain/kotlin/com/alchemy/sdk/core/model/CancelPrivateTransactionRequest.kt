package com.alchemy.sdk.core.model

import com.alchemy.sdk.util.HexString

data class CancelPrivateTransactionRequest(
    val txHash: HexString
)