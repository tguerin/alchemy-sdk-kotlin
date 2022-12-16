package com.alchemy.sdk.nft.model

import com.alchemy.sdk.nft.adapter.KOwnedNftsResponseSerializer
import kotlinx.serialization.Serializable


@Serializable(with = KOwnedNftsResponseSerializer::class)
sealed interface OwnedNftsResponse {
    /** The NFTs owned by the provided address. */
    val ownedNfts: List<Nft>

    /**
     * Pagination token that can be passed into another request to fetch the next
     * NFTs. If there is no page key, then there are no more NFTs to fetch.
     */
    val pageKey: String?

    /** The total count of NFTs owned by the provided address. */
    val totalCount: Long

    @Serializable
    data class OwnedBaseNftsResponse(
        override val ownedNfts: List<Nft.BaseNft>,
        override val pageKey: String? = null,
        override val totalCount: Long,
    ) : OwnedNftsResponse

    @Serializable
    data class OwnedAlchemyNftsResponse(
        override val ownedNfts: List<Nft.AlchemyNft>,
        override val pageKey: String? = null,
        override val totalCount: Long,
    ) : OwnedNftsResponse

}