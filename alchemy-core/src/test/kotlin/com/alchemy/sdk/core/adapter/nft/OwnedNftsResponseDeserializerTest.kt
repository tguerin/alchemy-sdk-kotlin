package com.alchemy.sdk.core.adapter.nft

import com.alchemy.sdk.core.model.core.Address
import com.alchemy.sdk.core.model.nft.NftContract
import com.alchemy.sdk.core.model.nft.NftId
import com.alchemy.sdk.core.model.nft.NftMetadata
import com.alchemy.sdk.core.model.nft.NftTokenType
import com.alchemy.sdk.core.model.nft.OwnedNft
import com.alchemy.sdk.core.model.nft.OwnedNftsResponse
import com.alchemy.sdk.core.model.nft.TokenMetadata
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

class OwnedNftsResponseDeserializerTest {

    @get:Rule
    val mockkRule = MockKRule(this)

    @MockK
    lateinit var context: JsonDeserializationContext

    @Test(expected = IllegalStateException::class)
    fun `should throw exception if value is not a json object`() {
        OwnedNftsResponseDeserializer.deserialize(
            JsonPrimitive(2),
            OwnedNftsResponse::class.java,
            context
        )
    }

    @Test
    fun `should parse owned nfts response as base nfts response`() {
        val expectedOwnedNftsResponse = OwnedNftsResponse.OwnedBaseNftsResponse(
            ownedNfts = listOf(
                OwnedNft.OwnedBaseNft(
                    balance = 1L,
                    contract = NftContract.BaseNftContract(Address.ContractAddress("0x0".hexString)),
                    id = NftId("1".hexString, TokenMetadata(NftTokenType.Erc721)),
                )
            ),
            pageKey = "pageKey",
            totalCount = 1L
        )
        val json = JsonObject().apply {
            add("ownedNfts", JsonArray().apply {
                add(
                    JsonObject().apply {
                        add("balance", JsonPrimitive(1L))
                        add("contract", JsonPrimitive("0x0"))
                        add("tokenId", JsonPrimitive("tokenId"))
                        add("tokenType", JsonPrimitive(NftTokenType.Erc721.value))
                    }
                )
            })
            add("pageKey", JsonPrimitive("pageKey"))
            add("totalCount", JsonPrimitive(1))
        }
        every {
            context.deserialize<OwnedNftsResponse.OwnedBaseNftsResponse>(
                json,
                OwnedNftsResponse.OwnedBaseNftsResponse::class.java
            )
        } returns expectedOwnedNftsResponse
        OwnedNftsResponseDeserializer.deserialize(
            json,
            OwnedNftsResponse::class.java,
            context
        ) shouldBeEqualTo expectedOwnedNftsResponse
    }

    @Test
    fun `should parse owned nfts response as alchemy nfts response`() {
        val rawMetadata = NftMetadata(emptyMap())
        val tokenUri = TokenUri("raw", "gateway")
        val expectedOwnedNftsResponse = OwnedNftsResponse.OwnedAlchemyNftsResponse(
            ownedNfts = listOf(
                OwnedNft.OwnedAlchemyNft(
                    balance = 1L,
                    contract = NftContract.AlchemyNftContract(Address.ContractAddress("0x0".hexString)),
                    id = NftId("1".hexString, TokenMetadata(NftTokenType.Erc721)),
                    title = "title",
                    description = "description",
                    timeLastUpdated = "134",
                    metadataError = null,
                    metadata = rawMetadata,
                    tokenUri = tokenUri,
                    media = emptyList()
                )
            ),
            pageKey = "pageKey",
            totalCount = 1L
        )
        val json = JsonObject().apply {
            add("ownedNfts", JsonArray().apply {
                add(
                    JsonObject().apply {
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
                )
            })
            add("pageKey", JsonPrimitive("pageKey"))
            add("totalCount", JsonPrimitive(1))
        }
        every {
            context.deserialize<OwnedNftsResponse.OwnedAlchemyNftsResponse>(
                json,
                OwnedNftsResponse.OwnedAlchemyNftsResponse::class.java
            )
        } returns expectedOwnedNftsResponse
        OwnedNftsResponseDeserializer.deserialize(
            json,
            OwnedNftsResponse::class.java,
            context
        ) shouldBeEqualTo expectedOwnedNftsResponse
    }

}