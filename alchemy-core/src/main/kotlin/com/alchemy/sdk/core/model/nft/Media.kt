package com.alchemy.sdk.core.model.nft

data class Media(
    /**
     * URI for the location of the NFT's original metadata blob for media (ex: the
     * original IPFS link).
     */
    val raw: String?,

    /** Public gateway URI for the raw URI. Generally offers better performance. */
    val gateway: String?,

    /** URL for a resized thumbnail of the NFT media asset. */
    val thumbnail: String?,

    /**
     * The media format (ex: jpg, gif, png) of the {@link gateway} and
     * {@link thumbnail} assets.
     */
    val format: String?,
)