package com.alchemy.sdk.core.model.nft

data class RefreshNftMetadataResponse(
    val updated: Boolean,
    val nft: Nft
)