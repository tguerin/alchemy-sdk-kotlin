package com.alchemy.sdk.nft.model

import com.alchemy.sdk.util.HexString
import kotlinx.serialization.Serializable

@Serializable
data class NftContractNftsResponse(
    val nfts: List<Nft>,
    val nextToken: HexString? = null
)