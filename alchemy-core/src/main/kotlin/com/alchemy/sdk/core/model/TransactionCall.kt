package com.alchemy.sdk.core.model

import com.alchemy.sdk.core.util.Ether
import com.alchemy.sdk.core.util.HexString

data class TransactionCall(
    val from: Address?,
    val to: Address,
    val gasPrice: Ether?,
    val value: String?,
    val data: HexString?
)