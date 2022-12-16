package com.alchemy.sdk.nft.model

import com.alchemy.sdk.core.model.Address
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RefreshContractResponse(
    val contractAddress: Address,
    @SerialName("reingestionState")
    val refreshState: RefreshState,
    val progress: Int? = null
)