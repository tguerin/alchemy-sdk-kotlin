package com.alchemy.sdk.nft.model

import com.alchemy.sdk.core.model.Address
import com.alchemy.sdk.nft.adapter.KNftContractSerializer
import kotlinx.serialization.Serializable

@Serializable(with = KNftContractSerializer::class)
sealed interface NftContract {
    fun address(): Address

    @Serializable
    open class BaseNftContract(val address: Address) : NftContract {
        override fun address(): Address {
            return address
        }

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other == null || this::class != other::class) return false

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

    @Serializable
    class AlchemyNftContract(
        val address: Address,
        private val contractMetadata: NftContractMetadata? = null
    ) : NftContract {
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

        override fun address(): Address {
            return address
        }

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other == null || this::class != other::class) return false

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

}