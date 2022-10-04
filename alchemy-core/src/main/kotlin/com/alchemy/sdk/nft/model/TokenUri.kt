package com.alchemy.sdk.nft.model

data class TokenUri(
    /**
     * URI for the location of the NFT's original metadata blob (ex: the original
     * IPFS link).
     */
    val raw: String,

    /** Public gateway URI for the raw URI. Generally offers better performance. */
    val gateway: String,
)