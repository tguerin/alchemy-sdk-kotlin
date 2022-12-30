package com.alchemy.sdk.nft.adapter

import com.alchemy.sdk.ResourceUtils.Companion.json
import com.alchemy.sdk.core.model.Address
import com.alchemy.sdk.nft.model.NftContract
import com.alchemy.sdk.nft.model.NftContractMetadata
import com.alchemy.sdk.nft.model.NftTokenType
import kotlinx.serialization.encodeToString
import com.alchemy.sdk.shouldBeEqualTo
import kotlin.test.Test

class NftContractSerializerTest {

    @Test
    fun `should serialize base nft contract`() {
        val nftContract = NftContract.BaseNftContract(
            Address.from("0xde0B295669a9FD93d5F28D9Ec85E40f4cb697BAe")
        )
        json.encodeToString(nftContract) shouldBeEqualTo "{\"address\":\"0xde0b295669a9fd93d5f28d9ec85e40f4cb697bae\"}"
    }

    @Test
    fun `should serialize alchemy nft contract`() {
        val nftContract = NftContract.AlchemyNftContract(
            address = Address.from("0xde0B295669a9FD93d5F28D9Ec85E40f4cb697BAe"),
            contractMetadata = NftContractMetadata(
                tokenType = NftTokenType.Erc721,
                name = "my token",
                symbol = "mt",
                totalSupply = 10L
            )
        )
        json.encodeToString(
            nftContract
        ) shouldBeEqualTo "{\"address\":\"0xde0b295669a9fd93d5f28d9ec85e40f4cb697bae\",\"contractMetadata\":{\"tokenType\":\"ERC721\",\"name\":\"my token\",\"symbol\":\"mt\",\"totalSupply\":10}}"
    }
}