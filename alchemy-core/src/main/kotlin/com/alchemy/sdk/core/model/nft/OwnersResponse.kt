package com.alchemy.sdk.core.model.nft

import com.alchemy.sdk.core.model.core.Address

data class OwnersResponse(
    val owners: List<Address>
)