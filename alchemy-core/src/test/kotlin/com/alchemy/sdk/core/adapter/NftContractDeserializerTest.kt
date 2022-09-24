package com.alchemy.sdk.core.adapter

import com.alchemy.sdk.core.adapter.nft.NftContractDeserializer
import com.alchemy.sdk.core.model.core.Address
import com.alchemy.sdk.core.model.nft.NftContract
import com.alchemy.sdk.core.model.nft.NftTokenType
import com.alchemy.sdk.core.util.HexString.Companion.hexString
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonNull
import com.google.gson.JsonObject
import com.google.gson.JsonPrimitive
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit4.MockKRule
import org.amshove.kluent.shouldBeEqualTo
import org.junit.Rule
import org.junit.Test

class NftContractDeserializerTest {

    @get:Rule
    val mockkRule = MockKRule(this)

    @MockK
    lateinit var context: JsonDeserializationContext

    @Test(expected = IllegalStateException::class)
    fun `should throw exception if value is not a json object`() {
        NftContractDeserializer.deserialize(
            JsonPrimitive(2),
            NftContract::class.java,
            context
        )
    }

    @Test
    fun `should parse nft contract as base nft contract`() {
        val expectedNftContract = NftContract.BaseNftContract(
            Address.ContractAddress("0x0".hexString)
        )
        val json = JsonObject().apply {
            add("contract", JsonPrimitive("0x0"))
        }
        every {
            context.deserialize<NftContract.BaseNftContract>(
                json,
                NftContract.BaseNftContract::class.java
            )
        } returns expectedNftContract
        NftContractDeserializer.deserialize(
            json,
            NftContract::class.java,
            context
        ) shouldBeEqualTo expectedNftContract
    }

    @Test
    fun `should parse nft contract as alchemy nft contract`() {
        val expectedNftContract = NftContract.AlchemyNftContract(
            address = Address.ContractAddress("0x0".hexString),
            tokenType = NftTokenType.Erc721,
            name = "my token",
            symbol = "mt",
            totalSupply = 10L
        )
        val json = JsonObject().apply {
            add("contract", JsonPrimitive("0x0"))
            add("tokenType", JsonPrimitive(NftTokenType.Erc721.value))
            add("name", JsonPrimitive("my token"))
            add("symbol", JsonPrimitive("mt"))
            add("totalSupply", JsonPrimitive(10L))
        }
        every {
            context.deserialize<NftContract.AlchemyNftContract>(
                json,
                NftContract.AlchemyNftContract::class.java
            )
        } returns expectedNftContract
        NftContractDeserializer.deserialize(
            json,
            NftContract::class.java,
            context
        ) shouldBeEqualTo expectedNftContract
    }

    @Test
    fun `should handle null case`() {
        val data = NftContractDeserializer.deserialize(
            JsonNull.INSTANCE,
            NftContract::class.java,
            context
        )
        data shouldBeEqualTo null
    }
}