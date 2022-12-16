package com.alchemy.sdk.nft.model

import kotlinx.serialization.Serializable

@Serializable
data class TokenMetadata(
    val tokenType: NftTokenType
)