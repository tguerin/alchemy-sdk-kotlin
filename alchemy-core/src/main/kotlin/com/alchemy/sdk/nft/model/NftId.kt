package com.alchemy.sdk.nft.model

import com.alchemy.sdk.util.HexString
import kotlinx.serialization.Serializable

@Serializable
data class NftId(
    val tokenId: HexString,
    val tokenMetadata: TokenMetadata? = null
)