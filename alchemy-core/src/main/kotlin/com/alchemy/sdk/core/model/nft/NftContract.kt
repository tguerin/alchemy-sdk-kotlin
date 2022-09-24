package com.alchemy.sdk.core.model.nft

import com.alchemy.sdk.core.model.core.Address

sealed interface NftContract {
    val address: Address

    data class BaseNftContract(
        override val address: Address
    ) : NftContract

    data class AlchemyNftContract(
        override val address: Address,
        /** The type of the token in the contract. */
        val tokenType: NftTokenType,
        /** The name of the contract. */
        val name: String?,
        /** The symbol of the contract. */
        val symbol: String?,
        /** The number of NFTs in the contract. */
        val totalSupply: Long?
    ) : NftContract

    companion object {
        val alchemyNftSpecificPropertyNames = listOf(
            "tokenType",
            "name",
            "symbol",
            "totalSupply",
        )
    }
}