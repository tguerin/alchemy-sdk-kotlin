package com.alchemy.sdk.nft.adapter

import com.alchemy.sdk.ResourceUtils.Companion.json
import com.alchemy.sdk.ResourceUtils.Companion.parseFile
import com.alchemy.sdk.core.model.Address
import com.alchemy.sdk.nft.model.Nft
import com.alchemy.sdk.nft.model.NftContract
import com.alchemy.sdk.nft.model.NftId
import com.alchemy.sdk.nft.model.NftMetadata
import com.alchemy.sdk.nft.model.NftTokenType
import com.alchemy.sdk.nft.model.TokenMetadata
import com.alchemy.sdk.nft.model.TokenUri
import com.alchemy.sdk.util.HexString.Companion.hexString
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import org.amshove.kluent.shouldBeEqualTo
import org.junit.Test

class NftDeserializerTest {


    @Test(expected = Exception::class)
    fun `should throw exception if value is not a json object`() {
        json.decodeFromString<Nft>("2")
    }

    @Test
    fun `should parse nft as base nft`() {
        val expectedNft = Nft.BaseNft(
            contract = NftContract.BaseNftContract(Address.from("0xbc4ca0eda7647a8ab7c2061c2e118a18a936f13d")),
            id = NftId("1".hexString, TokenMetadata(NftTokenType.Erc721))
        )

        json.decodeFromString<Nft>(
            "{\"contract\":{\"address\":\"0xbc4ca0eda7647a8ab7c2061c2e118a18a936f13d\"},\"id\":{\"tokenId\":\"0x01\",\"tokenMetadata\":{\"tokenType\":\"ERC721\"}}}"
        ) shouldBeEqualTo expectedNft
    }

    @Test
    fun `should parse nft as alchemy nft`() {
        val alchemyNft: Nft.AlchemyNft = parseFile("owned_nft_with_metadata.json")
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
                "date" to JsonPrimitive(1654933547421),
                "image" to JsonPrimitive("ipfs://bafybeifu4nlpuj5dniyvkxmd5fmghab7h2dfooprfpzm752hxhi7iou2yq/1.png"),
                "dna" to JsonPrimitive("172e42d0fc20a44ad5d2d10dcc09a37b8a75afe087d81c91375a3293b7532ab5"),
                "name" to JsonPrimitive("The Wanderer Lands #1"),
                "description" to JsonPrimitive("A beautiful and high resolution collection of A.I. generated landscapes"),
                "edition" to JsonPrimitive(1),
                "attributes" to JsonArray(
                    listOf(
                        JsonObject(
                            hashMapOf(
                                "value" to JsonPrimitive("The Legend Valley"),
                                "trait_type" to JsonPrimitive("Land")
                            )
                        ),
                        JsonObject(
                            hashMapOf(
                                "value" to JsonPrimitive("ColorFul"),
                                "trait_type" to JsonPrimitive("Style")
                            )
                        ),
                        JsonObject(
                            hashMapOf(
                                "value" to JsonPrimitive("Peeling Paint Lvl 7"),
                                "trait_type" to JsonPrimitive("Blend")
                            )
                        )
                    )
                )
            )
        )
    }

    @Test
    fun `should handle null case`() {
        val data = json.decodeFromString<Nft.BaseNft?>("null")
        data shouldBeEqualTo null
    }
}