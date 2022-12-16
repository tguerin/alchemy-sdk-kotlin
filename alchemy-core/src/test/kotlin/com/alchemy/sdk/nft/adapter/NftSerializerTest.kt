package com.alchemy.sdk.nft.adapter

import com.alchemy.sdk.ResourceUtils.Companion.json
import com.alchemy.sdk.ResourceUtils.Companion.parseFile
import com.alchemy.sdk.core.model.Address
import com.alchemy.sdk.nft.model.Nft
import com.alchemy.sdk.nft.model.NftContract
import com.alchemy.sdk.nft.model.NftId
import com.alchemy.sdk.nft.model.NftTokenType
import com.alchemy.sdk.nft.model.TokenMetadata
import com.alchemy.sdk.util.HexString.Companion.hexString
import kotlinx.serialization.encodeToString
import org.amshove.kluent.shouldBeEqualTo
import org.junit.Test

class NftSerializerTest {

    @Test
    fun `should serialize base nft`() {
        val expectedNft = Nft.BaseNft(
            contract = NftContract.BaseNftContract(Address.from("0xbc4ca0eda7647a8ab7c2061c2e118a18a936f13d")),
            id = NftId("1".hexString, TokenMetadata(NftTokenType.Erc721))
        )
        json.encodeToString<Nft>(
            expectedNft
        ) shouldBeEqualTo "{\"contract\":{\"address\":\"0xbc4ca0eda7647a8ab7c2061c2e118a18a936f13d\"},\"id\":{\"tokenId\":\"0x01\",\"tokenMetadata\":{\"tokenType\":\"ERC721\"}},\"balance\":null}".trim()
    }

    @Test
    fun `should serialize alchemy nft`() {
        val alchemyNft: Nft.AlchemyNft = parseFile("owned_nft_with_metadata.json")
        json.encodeToString(alchemyNft) shouldBeEqualTo "{\"contract\":{\"address\":\"0x4b076f0e07eed3f1007fb1b5c000f7a08d3208e1\",\"contractMetadata\":null},\"id\":{\"tokenId\":\"0x0000000000000000000000000000000000000000000000000000000000000001\",\"tokenMetadata\":{\"tokenType\":\"ERC721\"}},\"balance\":1,\"title\":\"The Wanderer Lands #1\",\"description\":\"A beautiful and high resolution collection of A.I. generated landscapes\",\"timeLastUpdated\":\"2022-06-17T20:28:30.339Z\",\"metadataError\":null,\"metadata\":{\"date\":1654933547421,\"image\":\"ipfs://bafybeifu4nlpuj5dniyvkxmd5fmghab7h2dfooprfpzm752hxhi7iou2yq/1.png\",\"dna\":\"172e42d0fc20a44ad5d2d10dcc09a37b8a75afe087d81c91375a3293b7532ab5\",\"name\":\"The Wanderer Lands #1\",\"description\":\"A beautiful and high resolution collection of A.I. generated landscapes\",\"edition\":1,\"attributes\":[{\"value\":\"The Legend Valley\",\"trait_type\":\"Land\"},{\"value\":\"ColorFul\",\"trait_type\":\"Style\"},{\"value\":\"Peeling Paint Lvl 7\",\"trait_type\":\"Blend\"}]},\"tokenUri\":{\"raw\":\"ipfs://bafybeifcnkipzxl54rk523qhorqw5ysrvdppxg4vykkuhh4vrqqhoeyfiy/1.json\",\"gateway\":\"https://ipfs.io/ipfs/bafybeifcnkipzxl54rk523qhorqw5ysrvdppxg4vykkuhh4vrqqhoeyfiy/1.json\"},\"media\":[{\"raw\":\"ipfs://bafybeifu4nlpuj5dniyvkxmd5fmghab7h2dfooprfpzm752hxhi7iou2yq/1.png\",\"gateway\":\"https://ipfs.io/ipfs/bafybeifu4nlpuj5dniyvkxmd5fmghab7h2dfooprfpzm752hxhi7iou2yq/1.png\",\"thumbnail\":null,\"format\":null,\"bytes\":null}],\"contractMetadata\":{\"tokenType\":\"ERC721\",\"name\":\"AILandscape\",\"symbol\":\"AILAND\",\"totalSupply\":9}}"
    }
}