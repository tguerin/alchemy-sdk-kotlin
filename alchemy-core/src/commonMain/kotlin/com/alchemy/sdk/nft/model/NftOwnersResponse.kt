package com.alchemy.sdk.nft.model

import com.alchemy.sdk.core.model.Address
import kotlinx.serialization.Serializable

@Serializable
data class NftOwnersResponse(
    val owners: List<Address>
)