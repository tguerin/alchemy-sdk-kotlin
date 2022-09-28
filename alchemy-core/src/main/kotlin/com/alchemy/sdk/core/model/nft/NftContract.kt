package com.alchemy.sdk.core.model.nft

import com.alchemy.sdk.core.model.core.Address
import com.google.gson.annotations.Expose

sealed interface NftContract {
    val address: Address

    open class BaseNftContract(
        override val address: Address
    ) : NftContract {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as BaseNftContract

            if (address != other.address) return false

            return true
        }

        override fun hashCode(): Int {
            return address.hashCode()
        }

        override fun toString(): String {
            return "BaseNftContract(address=$address)"
        }

    }

    class AlchemyNftContract(
        address: Address,
        @Expose(serialize = false, deserialize = true)
        private val contractMetadata: NftContractMetadata? = null
    ) : BaseNftContract(address) {
        /** The type of the token in the contract. */
        val tokenType: NftTokenType
            get() = contractMetadata?.tokenType ?: NftTokenType.Unknown

        /** The name of the contract. */
        val name: String?
            get() = contractMetadata?.name

        /** The symbol of the contract. */

        val symbol: String?
            get() = contractMetadata?.symbol

        /** The number of NFTs in the contract. */
        val totalSupply: Long?
            get() = contractMetadata?.totalSupply

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as AlchemyNftContract

            if (contractMetadata != other.contractMetadata) return false

            return true
        }

        override fun hashCode(): Int {
            return contractMetadata?.hashCode() ?: 0
        }

        override fun toString(): String {
            return "AlchemyNftContract(contractMetadata=$contractMetadata)"
        }

    }

    companion object {
        val alchemyNftSpecificPropertyNames = listOf(
            "tokenType",
            "name",
            "symbol",
            "totalSupply",
        )
    }
}