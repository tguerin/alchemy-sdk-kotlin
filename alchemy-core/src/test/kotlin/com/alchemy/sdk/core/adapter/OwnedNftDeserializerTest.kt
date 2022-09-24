package com.alchemy.sdk.core.adapter

import com.alchemy.sdk.core.adapter.nft.OwnedNftDeserializer
import com.alchemy.sdk.core.model.core.Address
import com.alchemy.sdk.core.model.nft.NftContract
import com.alchemy.sdk.core.model.nft.NftMetadata
import com.alchemy.sdk.core.model.nft.NftTokenType
import com.alchemy.sdk.core.model.nft.OwnedNft
import com.alchemy.sdk.core.model.nft.TokenUri
import com.alchemy.sdk.core.util.GsonUtil.Companion.gson
import com.alchemy.sdk.core.util.HexString.Companion.hexString
import com.google.gson.JsonArray
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

class OwnedNftDeserializerTest {

    @get:Rule
    val mockkRule = MockKRule(this)

    @MockK
    lateinit var context: JsonDeserializationContext

    @Test(expected = IllegalStateException::class)
    fun `should throw exception if value is not a json object`() {
        OwnedNftDeserializer.deserialize(
            JsonPrimitive(2),
            OwnedNft::class.java,
            context
        )
    }

    @Test
    fun `should parse owned nft as base nft`() {
        val expectedOwnedNft = OwnedNft.OwnedBaseNft(
            balance = 1L,
            contract = NftContract.BaseNftContract(Address.ContractAddress("0x0".hexString)),
            tokenId = "tokenId",
            tokenType = NftTokenType.Erc721
        )
        val json = JsonObject().apply {
            add("balance", JsonPrimitive(1L))
            add("contract", JsonPrimitive("0x0"))
            add("tokenId", JsonPrimitive("tokenId"))
            add("tokenType", JsonPrimitive(NftTokenType.Erc721.value))
        }
        every {
            context.deserialize<OwnedNft.OwnedBaseNft>(
                json,
                OwnedNft.OwnedBaseNft::class.java
            )
        } returns expectedOwnedNft
        OwnedNftDeserializer.deserialize(
            json,
            OwnedNft::class.java,
            context
        ) shouldBeEqualTo expectedOwnedNft
    }

    @Test
    fun `should parse owned nft as alchemy nft`() {
        val rawMetadata = NftMetadata(emptyMap())
        val tokenUri = TokenUri("raw", "gateway")
        val expectedOwnedNft = OwnedNft.OwnedAlchemyNft(
            balance = 1L,
            contract = NftContract.BaseNftContract(Address.ContractAddress("0x0".hexString)),
            tokenId = "tokenId",
            tokenType = NftTokenType.Erc721,
            title = "title",
            description = "description",
            timeLastUpdated = "134",
            metadataError = null,
            rawMetadata = rawMetadata,
            tokenUri = tokenUri,
            media = emptyList()
        )
        val json = JsonObject().apply {
            add("balance", JsonPrimitive(1L))
            add("contract", JsonPrimitive("0x0"))
            add("tokenId", JsonPrimitive("tokenId"))
            add("tokenType", JsonPrimitive(NftTokenType.Erc721.value))
            add("title", JsonPrimitive("title"))
            add("description", JsonPrimitive("description"))
            add("metadataError", JsonNull.INSTANCE)
            add("rawMetadata", gson.toJsonTree(rawMetadata))
            add("tokenUri", gson.toJsonTree(tokenUri))
            add("media", JsonArray())
        }
        every {
            context.deserialize<OwnedNft.OwnedAlchemyNft>(
                json,
                OwnedNft.OwnedAlchemyNft::class.java
            )
        } returns expectedOwnedNft
        OwnedNftDeserializer.deserialize(
            json,
            OwnedNft::class.java,
            context
        ) shouldBeEqualTo expectedOwnedNft
    }

    @Test
    fun `should handle null case`() {
        val data = OwnedNftDeserializer.deserialize(
            JsonNull.INSTANCE,
            OwnedNft::class.java,
            context
        )
        data shouldBeEqualTo null
    }
}