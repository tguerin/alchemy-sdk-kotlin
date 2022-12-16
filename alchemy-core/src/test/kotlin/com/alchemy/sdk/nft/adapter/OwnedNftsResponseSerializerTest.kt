package com.alchemy.sdk.nft.adapter

import com.alchemy.sdk.ResourceUtils.Companion.json
import com.alchemy.sdk.core.model.Address
import com.alchemy.sdk.nft.model.Nft
import com.alchemy.sdk.nft.model.NftContract
import com.alchemy.sdk.nft.model.NftId
import com.alchemy.sdk.nft.model.NftMetadata
import com.alchemy.sdk.nft.model.NftTokenType
import com.alchemy.sdk.nft.model.OwnedNftsResponse
import com.alchemy.sdk.nft.model.TokenMetadata
import com.alchemy.sdk.nft.model.TokenUri
import com.alchemy.sdk.util.HexString.Companion.hexString
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import org.amshove.kluent.shouldBeEqualTo
import org.junit.Test

class OwnedNftsResponseSerializerTest {

    @Test
    fun `should parse owned nfts response as base nfts response`() {
        val ownedNftsResponse = OwnedNftsResponse.OwnedBaseNftsResponse(
            ownedNfts = listOf(
                Nft.BaseNft(
                    balance = 1L,
                    contract = NftContract.BaseNftContract(Address.from("0xbc4ca0eda7647a8ab7c2061c2e118a18a936f13d")),
                    id = NftId("1".hexString, TokenMetadata(NftTokenType.Erc721)),
                )
            ),
            pageKey = "pageKey",
            totalCount = 1L
        )
        json.encodeToString(
            ownedNftsResponse
        ) shouldBeEqualTo "{\"ownedNfts\":[{\"contract\":{\"address\":\"0xbc4ca0eda7647a8ab7c2061c2e118a18a936f13d\"},\"id\":{\"tokenId\":\"0x01\",\"tokenMetadata\":{\"tokenType\":\"ERC721\"}},\"balance\":1}],\"pageKey\":\"pageKey\",\"totalCount\":1}"
    }

    @Test
    fun `should parse owned nfts response as alchemy nfts response`() {
        val rawMetadata = NftMetadata(emptyMap())
        val tokenUri = TokenUri("raw", "gateway")
        val expectedOwnedNftsResponse = OwnedNftsResponse.OwnedAlchemyNftsResponse(
            ownedNfts = listOf(
                Nft.AlchemyNft(
                    balance = 1L,
                    contract = NftContract.AlchemyNftContract(Address.from("0xbc4ca0eda7647a8ab7c2061c2e118a18a936f13d")),
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
        json.decodeFromString<OwnedNftsResponse.OwnedAlchemyNftsResponse>(
            "{\"ownedNfts\":[{\"contract\":{\"address\":\"0xbc4ca0eda7647a8ab7c2061c2e118a18a936f13d\"},\"id\":{\"tokenId\":\"0x01\",\"tokenMetadata\":{\"tokenType\":\"ERC721\"}},\"balance\":1,\"title\":\"title\",\"description\":\"description\",\"timeLastUpdated\":\"134\",\"metadataError\":null,\"metadata\":{},\"tokenUri\":{\"raw\":\"raw\",\"gateway\":\"gateway\"},\"media\":[]}],\"pageKey\":\"pageKey\",\"totalCount\":1}"
        ) shouldBeEqualTo expectedOwnedNftsResponse
    }

}