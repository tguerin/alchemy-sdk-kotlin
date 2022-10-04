package com.alchemy.sdk.core.model

import com.alchemy.sdk.util.Ether
import com.alchemy.sdk.util.HexString

data class TransactionCall(
    val to: Address,
    val from: Address? = null,
    val gasPrice: Ether? = null,
    val value: String? = null,
    val data: HexString? = null,
    val ccipReadEnabled: Boolean? = null,
)