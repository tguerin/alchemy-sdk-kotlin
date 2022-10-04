package com.alchemy.sdk.nft.model

sealed interface OwnedNft {
    /** The token balance of the NFT. */
    val balance: Long

    class OwnedBaseNft(
        override val balance: Long,
        contract: NftContract.BaseNftContract,
        id: NftId,
    ) : Nft.BaseNft(contract, id), OwnedNft {

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false
            if (!super.equals(other)) return false

            other as OwnedBaseNft

            if (balance != other.balance) return false

            return true
        }

        override fun hashCode(): Int {
            var result = super.hashCode()
            result = 31 * result + balance.hashCode()
            return result
        }

        override fun toString(): String {
            return "OwnedBaseNft(balance=$balance, ${super.toString()})"
        }

    }

    class OwnedAlchemyNft(
        override val balance: Long,
        contract: NftContract.AlchemyNftContract,
        id: NftId,
        title: String,
        description: String,
        timeLastUpdated: String,
        metadataError: String?,
        metadata: NftMetadata,
        tokenUri: TokenUri,
        media: List<Media>,
    ) : Nft.AlchemyNft(
        contract,
        id,
        title,
        description,
        timeLastUpdated,
        metadataError,
        metadata,
        tokenUri,
        media
    ), OwnedNft {

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false
            if (!super.equals(other)) return false

            other as OwnedAlchemyNft

            if (balance != other.balance) return false

            return true
        }

        override fun hashCode(): Int {
            var result = super.hashCode()
            result = 31 * result + balance.hashCode()
            return result
        }

        override fun toString(): String {
            return "OwnedAlchemyNft(balance=$balance, ${super.toString()})"
        }

    }
}