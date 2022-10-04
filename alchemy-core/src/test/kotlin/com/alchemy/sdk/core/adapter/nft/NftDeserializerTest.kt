package com.alchemy.sdk.core.adapter.nft

import com.alchemy.sdk.core.ResourceUtils.Companion.parseFile
import com.alchemy.sdk.core.model.core.Address
import com.alchemy.sdk.core.model.nft.Nft
import com.alchemy.sdk.core.model.nft.NftContract
import com.alchemy.sdk.core.model.nft.NftId
import com.alchemy.sdk.core.model.nft.NftMetadata
import com.alchemy.sdk.core.model.nft.NftTokenType
import com.alchemy.sdk.core.model.nft.OwnedNft
import com.alchemy.sdk.core.model.nft.TokenMetadata
import com.alchemy.sdk.core.model.nft.TokenUri
import com.alchemy.sdk.core.util.GsonUtil.Companion.nftGson
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

class NftDeserializerTest {

    @get:Rule
    val mockkRule = MockKRule(this)

    @MockK
    lateinit var context: JsonDeserializationContext

    @Test(expected = IllegalStateException::class)
    fun `should throw exception if value is not a json object`() {
        NftDeserializer.deserialize(
            JsonPrimitive(2),
            OwnedNft::class.java,
            context
        )
    }

    @Test
    fun `should parse nft as base nft`() {
        val expectedNft = Nft.BaseNft(
            contract = NftContract.BaseNftContract(Address.ContractAddress("0x0".hexString)),
            id = NftId("1".hexString, TokenMetadata(NftTokenType.Erc721))
        )
        val json = JsonObject().apply {
            add("balance", JsonPrimitive(1L))
            add("contract", JsonPrimitive("0x0"))
            add("tokenId", JsonPrimitive("1"))
            add("tokenType", JsonPrimitive(NftTokenType.Erc721.value))
        }
        every {
            context.deserialize<Nft.BaseNft>(
                json,
                Nft.BaseNft::class.java
            )
        } returns expectedNft
        NftDeserializer.deserialize(
            json,
            Nft::class.java,
            context
        ) shouldBeEqualTo expectedNft
    }

    @Test
    fun `should parse nft as alchemy nft`() {
        val alchemyNft = parseFile(
            "owned_nft_with_metadata.json",
            Nft.AlchemyNft::class.java,
            nftGson
        )
        alchemyNft.contract.address.value shouldBeEqualTo "0x4b076f0e07eed3f1007fb1b5c000f7a08d3208e1".hexString
        alchemyNft.tokenId shouldBeEqualTo "0x0000000000000000000000000000000000000000000000000000000000000001".hexString
        alchemyNft.tokenType shouldBeEqualTo NftTokenType.Erc721
        alchemyNft.title shouldBeEqualTo "The Wanderer Lands #1"
        alchemyNft.description shouldBeEqualTo "A beautiful and high resolution collection of A.I. generated landscapes"
        alchemyNft.timeLastUpdated shouldBeEqualTo "2022-06-17T20:28:30.339Z"
        alchemyNft.metadataError shouldBeEqualTo null
        alchemyNft.tokenUri shouldBeEqualTo TokenUri(
            "ipfs://bafybeifcnkipzxl54rk523qhorqw5ysrvdppxg4vykkuhh4vrqqhoeyfiy/1.json",
            "https://ipfs.io/ipfs/bafybeifcnkipzxl54rk523qhorqw5ysrvdppxg4vykkuhh4vrqqhoeyfiy/1.json"
        )
        alchemyNft.metadata shouldBeEqualTo NftMetadata(
            hashMapOf(
                "date" to 1.654933547421E12,
                "image" to "ipfs://bafybeifu4nlpuj5dniyvkxmd5fmghab7h2dfooprfpzm752hxhi7iou2yq/1.png",
                "dna" to "172e42d0fc20a44ad5d2d10dcc09a37b8a75afe087d81c91375a3293b7532ab5",
                "name" to "The Wanderer Lands #1",
                "description" to "A beautiful and high resolution collection of A.I. generated landscapes",
                "edition" to 1.0,
                "attributes" to listOf(
                    hashMapOf(
                        "value" to "The Legend Valley",
                        "trait_type" to "Land"
                    ),
                    hashMapOf(
                        "value" to "ColorFul",
                        "trait_type" to "Style"
                    ),
                    hashMapOf(
                        "value" to "Peeling Paint Lvl 7",
                        "trait_type" to "Blend"
                    )
                )
            )
        )
    }

    @Test
    fun `should handle null case`() {
        val data = NftDeserializer.deserialize(
            JsonNull.INSTANCE,
            NftDeserializer::class.java,
            context
        )
        data shouldBeEqualTo null
    }
}