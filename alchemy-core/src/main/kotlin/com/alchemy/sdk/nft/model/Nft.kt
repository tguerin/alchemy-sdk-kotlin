package com.alchemy.sdk.nft.model

import com.alchemy.sdk.nft.adapter.KNftSerializer
import com.alchemy.sdk.util.HexString
import kotlinx.serialization.Serializable

@Serializable(with = KNftSerializer::class)
sealed interface Nft {

    val contract: NftContract?

    /** The NFT token ID as an integer string. */
    val tokenId: HexString

    /** The type of ERC token, if known. */
    val tokenType: NftTokenType

    /** The quantity of owned Nft. */
    val balance: Long?

    @Serializable
    class BaseNft(
        override val contract: NftContract.BaseNftContract? = null,
        private val id: NftId,
        override val balance: Long? = null,
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

    @Serializable
    class AlchemyNft(
        override val contract: NftContract.AlchemyNftContract,

        private val id: NftId,

        override val balance: Long? = null,

        /** The NFT title. */
        val title: String,

        /** The NFT description. */
        val description: String,

        /** When the NFT was last updated in the blockchain. Represented in ISO-8601 format. */
        val timeLastUpdated: String,

        /** Holds an error message if there was an issue fetching metadata. */
        val metadataError: String? = null,

        /**
         * The raw metadata fetched from the metadata URL specified by the NFT. The
         * field is undefined if Alchemy was unable to fetch metadata.
         */
        val metadata: NftMetadata? = null,

        /** URIs for accessing the NFT's metadata blob. */
        val tokenUri: TokenUri,

        /** URIs for accessing the NFT's media assets. */
        val media: List<Media> = emptyList(),

        /** Contract metadata */
        val contractMetadata: NftContractMetadata? = null
    ) : Nft {


        override fun toString(): String {
            return "AlchemyNft(title='$title', description='$description', timeLastUpdated='$timeLastUpdated', metadataError=$metadataError, metadata=$metadata, tokenUri=$tokenUri, media=$media)"
        }

        override val tokenId: HexString
            get() = id.tokenId
        override val tokenType: NftTokenType
            get() = id.tokenMetadata?.tokenType ?: NftTokenType.Unknown

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

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
}