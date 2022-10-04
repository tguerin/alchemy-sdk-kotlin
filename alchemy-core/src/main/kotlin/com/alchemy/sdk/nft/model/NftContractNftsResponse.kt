package com.alchemy.sdk.nft.model

import com.alchemy.sdk.util.HexString

data class NftContractNftsResponse(
    val nfts: List<Nft>,
    val nextToken: HexString?
)