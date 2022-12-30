package com.alchemy.sdk.nft.adapter

import com.alchemy.sdk.ResourceUtils.Companion.json
import com.alchemy.sdk.core.model.Address
import com.alchemy.sdk.nft.model.NftContract
import com.alchemy.sdk.nft.model.NftContractMetadata
import com.alchemy.sdk.nft.model.NftTokenType
import kotlinx.serialization.decodeFromString
import com.alchemy.sdk.shouldBeEqualTo
import kotlin.test.Test
import kotlin.test.assertFailsWith

class NftContractDeserializerTest {

    @Test
    fun `should throw exception if value is not a json object`() {
        assertFailsWith<Exception> {
            json.decodeFromString<NftContract>("2")
        }
    }

    @Test
    fun `should parse nft contract as base nft contract`() {
        val expectedNftContract = NftContract.BaseNftContract(
            Address.from("0xde0B295669a9FD93d5F28D9Ec85E40f4cb697BAe")
        )
        json.decodeFromString<NftContract>("{\"address\": \"0xde0B295669a9FD93d5F28D9Ec85E40f4cb697BAe\"}") shouldBeEqualTo expectedNftContract
    }

    @Test
    fun `should parse nft contract as alchemy nft contract`() {
        val expectedNftContract = NftContract.AlchemyNftContract(
            address = Address.from("0xde0B295669a9FD93d5F28D9Ec85E40f4cb697BAe"),
            contractMetadata = NftContractMetadata(
                tokenType = NftTokenType.Erc721,
                name = "my token",
                symbol = "mt",
                totalSupply = 10L
            )
        )
        json.decodeFromString<NftContract>(
            "{\"address\":\"0xde0b295669a9fd93d5f28d9ec85e40f4cb697bae\",\"contractMetadata\":{\"tokenType\":\"Erc721\",\"name\":\"my token\",\"symbol\":\"mt\",\"totalSupply\":10}}"
        ) shouldBeEqualTo expectedNftContract
    }

    @Test
    fun `should handle null case`() {
        val data = json.decodeFromString<NftContract?>(
            "null"
        )
        data shouldBeEqualTo null
    }
}