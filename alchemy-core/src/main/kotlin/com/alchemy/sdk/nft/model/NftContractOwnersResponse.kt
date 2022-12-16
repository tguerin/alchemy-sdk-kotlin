package com.alchemy.sdk.nft.model

import com.alchemy.sdk.core.model.Address
import kotlinx.serialization.Serializable

@Serializable
data class NftContractOwnersResponse(
    val ownerAddresses: List<Address>
)
