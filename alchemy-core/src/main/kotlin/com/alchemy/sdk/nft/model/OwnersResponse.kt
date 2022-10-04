package com.alchemy.sdk.nft.model

import com.alchemy.sdk.core.model.Address

data class OwnersResponse(
    val owners: List<Address>
)