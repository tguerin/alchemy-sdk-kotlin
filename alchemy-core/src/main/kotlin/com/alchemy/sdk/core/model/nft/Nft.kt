package com.alchemy.sdk.core.model.nft

sealed interface Nft {

    val contract: NftContract.BaseNftContract

    /** The NFT token ID as an integer string. */
    val tokenId: String

    /** The type of ERC token, if known. */
    val tokenType: NftTokenType

    open class BaseNft(
        override val contract: NftContract.BaseNftContract,

        /** The NFT token ID as an integer string. */
        override val tokenId: String,

        /** The type of ERC token, if known. */
        override val tokenType: NftTokenType,
    ) : Nft

    open class AlchemyNft(
        override val contract: NftContract.BaseNftContract,

        /** The NFT token ID as an integer string. */
        override val tokenId: String,

        /** The type of ERC token, if known. */
        override val tokenType: NftTokenType,

        /** The NFT title. */
        val title: String,

        /** The NFT description. */
        val description: String,

        /** When the NFT was last updated in the blockchain. Represented in ISO-8601 format. */
        val timeLastUpdated: String,

        /** Holds an error message if there was an issue fetching metadata. */
        val metadataError: String?,

        /**
         * The raw metadata fetched from the metadata URL specified by the NFT. The
         * field is undefined if Alchemy was unable to fetch metadata.
         */
        val rawMetadata: NftMetadata,

        /** URIs for accessing the NFT's metadata blob. */
        val tokenUri: TokenUri,

        /** URIs for accessing the NFT's media assets. */
        val media: List<Media>,
    ) : Nft

    companion object {
        val alchemyNftSpecificPropertyNames = listOf(
            "title",
            "description",
            "timeLastUpdated",
            "metadataError",
            "rawMetadata",
            "tokenUri",
            "media",
        )
    }
}