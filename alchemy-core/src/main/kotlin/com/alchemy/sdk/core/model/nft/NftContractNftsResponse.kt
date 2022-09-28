package com.alchemy.sdk.core.model.nft

data class NftContractNftsResponse(
    val nfts: List<Nft>,
    val pageKey: String?
)