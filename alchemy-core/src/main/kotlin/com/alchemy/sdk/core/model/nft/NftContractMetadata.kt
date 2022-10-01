package com.alchemy.sdk.core.model.nft

data class NftContractMetadata(
    /** The type of the token in the contract. */
    val tokenType: NftTokenType = NftTokenType.Unknown,
    /** The name of the contract. */
    val name: String? = null,
    /** The symbol of the contract. */
    val symbol: String? = null,
    /** The number of NFTs in the contract. */
    val totalSupply: Long? = null,
)