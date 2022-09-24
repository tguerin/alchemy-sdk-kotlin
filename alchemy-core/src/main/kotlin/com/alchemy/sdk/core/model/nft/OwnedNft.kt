package com.alchemy.sdk.core.model.nft

sealed interface OwnedNft {
    /** The token balance of the NFT. */
    val balance: Long

    class OwnedBaseNft(
        override val balance: Long,
        contract: NftContract.BaseNftContract,
        tokenId: String,
        tokenType: NftTokenType
    ) : Nft.BaseNft(contract, tokenId, tokenType), OwnedNft

    class OwnedAlchemyNft(
        override val balance: Long,
        contract: NftContract.BaseNftContract,
        tokenId: String,
        tokenType: NftTokenType,
        title: String,
        description: String,
        timeLastUpdated: String,
        metadataError: String?,
        rawMetadata: NftMetadata,
        tokenUri: TokenUri,
        media: List<Media>,
    ) : Nft.AlchemyNft(
        contract,
        tokenId,
        tokenType,
        title,
        description,
        timeLastUpdated,
        metadataError,
        rawMetadata,
        tokenUri,
        media
    ), OwnedNft
}