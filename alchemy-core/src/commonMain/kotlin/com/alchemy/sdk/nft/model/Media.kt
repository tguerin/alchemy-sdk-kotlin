package com.alchemy.sdk.nft.model

import kotlinx.serialization.Serializable

@Serializable
data class Media(
    /**
     * URI for the location of the NFT's original metadata blob for media (ex: the
     * original IPFS link).
     */
    val raw: String? = null,

    /** Public gateway URI for the raw URI. Generally offers better performance. */
    val gateway: String? = null,

    /** URL for a resized thumbnail of the NFT media asset. */
    val thumbnail: String? = null,

    /**
     * The media format (ex: jpg, gif, png) of the {@link gateway} and
     * {@link thumbnail} assets.
     */
    val format: String? = null,

    /**
     * Media bytes count
     */
    val bytes: Long? = null
)