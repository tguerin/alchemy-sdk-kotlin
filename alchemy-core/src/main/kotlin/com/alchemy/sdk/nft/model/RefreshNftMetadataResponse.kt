package com.alchemy.sdk.nft.model

import kotlinx.serialization.Serializable

@Serializable
data class RefreshNftMetadataResponse(
    val updated: Boolean,
    val nft: Nft
)