package com.alchemy.sdk.core.model.nft

import com.alchemy.sdk.core.util.HexString

data class NftContractNftsResponse(
    val nfts: List<Nft>,
    val nextToken: HexString?
)