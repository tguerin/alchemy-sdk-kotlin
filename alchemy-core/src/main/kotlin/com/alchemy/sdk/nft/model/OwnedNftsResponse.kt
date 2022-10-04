package com.alchemy.sdk.nft.model

sealed interface OwnedNftsResponse {
    /** The NFTs owned by the provided address. */
    val ownedNfts: List<OwnedNft>

    /**
     * Pagination token that can be passed into another request to fetch the next
     * NFTs. If there is no page key, then there are no more NFTs to fetch.
     */
    val pageKey: String?

    /** The total count of NFTs owned by the provided address. */
    val totalCount: Long

    data class OwnedBaseNftsResponse(
        override val ownedNfts: List<OwnedNft.OwnedBaseNft>,
        override val pageKey: String?,
        override val totalCount: Long,
    ) : OwnedNftsResponse

    data class OwnedAlchemyNftsResponse(
        override val ownedNfts: List<OwnedNft.OwnedAlchemyNft>,
        override val pageKey: String?,
        override val totalCount: Long,
    ) : OwnedNftsResponse

}