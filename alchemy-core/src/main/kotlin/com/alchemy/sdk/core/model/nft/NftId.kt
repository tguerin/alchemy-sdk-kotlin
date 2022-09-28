package com.alchemy.sdk.core.model.nft

import com.alchemy.sdk.core.util.HexString

data class NftId(
    val tokenId: HexString,
    val tokenMetadata: TokenMetadata
)