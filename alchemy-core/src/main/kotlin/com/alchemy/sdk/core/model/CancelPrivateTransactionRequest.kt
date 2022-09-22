package com.alchemy.sdk.core.model

import com.alchemy.sdk.core.util.HexString

data class CancelPrivateTransactionRequest(
    val txHash: HexString
)