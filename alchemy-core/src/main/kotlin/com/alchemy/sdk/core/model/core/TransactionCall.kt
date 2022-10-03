package com.alchemy.sdk.core.model.core

import com.alchemy.sdk.core.util.Ether
import com.alchemy.sdk.core.util.HexString

data class TransactionCall(
    val to: Address,
    val from: Address? = null,
    val gasPrice: Ether? = null,
    val value: String? = null,
    val data: HexString? = null,
    val ccipReadEnabled: Boolean? = null,
)