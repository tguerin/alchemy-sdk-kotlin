package com.alchemy.sdk.ws.model

import com.alchemy.sdk.core.model.Address
import com.alchemy.sdk.core.model.BlockTag
import com.alchemy.sdk.util.HexString
import kotlinx.serialization.Serializable

@Serializable
data class LogFilterCall(
    val address: Address? = null,
    val topics: List<HexString> = emptyList(),
    val fromBlock: BlockTag? = null,
    val toBlock: BlockTag? = null,
)