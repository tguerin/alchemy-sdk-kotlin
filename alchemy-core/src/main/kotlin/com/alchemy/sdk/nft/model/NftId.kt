package com.alchemy.sdk.nft.model

import com.alchemy.sdk.util.HexString

data class NftId(
    val tokenId: HexString,
    val tokenMetadata: TokenMetadata? = null
)