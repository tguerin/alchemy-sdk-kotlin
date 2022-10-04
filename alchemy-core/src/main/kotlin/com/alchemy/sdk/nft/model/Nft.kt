package com.alchemy.sdk.nft.model

import com.alchemy.sdk.util.HexString
import com.google.gson.annotations.Expose

sealed interface Nft {

    val contract: NftContract.BaseNftContract

    /** The NFT token ID as an integer string. */
    val tokenId: HexString

    /** The type of ERC token, if known. */
    val tokenType: NftTokenType

    open class BaseNft(
        override val contract: NftContract.BaseNftContract,
        @Expose(serialize = false, deserialize = true)
        private val id: NftId,
    ) : Nft {
        override val tokenId: HexString
            get() = id.tokenId
        override val tokenType: NftTokenType
            get() = id.tokenMetadata?.tokenType ?: NftTokenType.Unknown

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as BaseNft

            if (contract != other.contract) return false
            if (id != other.id) return false

            return true
        }

        override fun hashCode(): Int {
            var result = contract.hashCode()
            result = 31 * result + id.hashCode()
            return result
        }

        override fun toString(): String {
            return "BaseNft(contract=$contract, id=$id)"
        }

    }

    open class AlchemyNft(
        contract: NftContract.BaseNftContract,

        id: NftId,

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
        val metadata: NftMetadata?,

        /** URIs for accessing the NFT's metadata blob. */
        val tokenUri: TokenUri,

        /** URIs for accessing the NFT's media assets. */
        val media: List<Media>
    ) : BaseNft(contract, id) {


        override fun toString(): String {
            return "AlchemyNft(title='$title', description='$description', timeLastUpdated='$timeLastUpdated', metadataError=$metadataError, metadata=$metadata, tokenUri=$tokenUri, media=$media)"
        }

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false
            if (!super.equals(other)) return false

            other as AlchemyNft

            if (title != other.title) return false
            if (description != other.description) return false
            if (timeLastUpdated != other.timeLastUpdated) return false
            if (metadataError != other.metadataError) return false
            if (metadata != other.metadata) return false
            if (tokenUri != other.tokenUri) return false
            if (media != other.media) return false

            return true
        }

        override fun hashCode(): Int {
            var result = super.hashCode()
            result = 31 * result + title.hashCode()
            result = 31 * result + description.hashCode()
            result = 31 * result + timeLastUpdated.hashCode()
            result = 31 * result + (metadataError?.hashCode() ?: 0)
            result = 31 * result + (metadata?.hashCode() ?: 0)
            result = 31 * result + tokenUri.hashCode()
            result = 31 * result + media.hashCode()
            return result
        }

    }


    companion object {
        val alchemyNftSpecificPropertyNames = listOf(
            "title",
            "description",
            "timeLastUpdated",
            "metadataError",
            "rawMetadata",
            "tokenUri",
            "media",
            "contractMetadata",
        )
    }
}