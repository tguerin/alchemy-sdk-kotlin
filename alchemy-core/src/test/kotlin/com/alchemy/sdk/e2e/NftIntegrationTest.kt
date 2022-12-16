package com.alchemy.sdk.e2e

import com.alchemy.sdk.Alchemy
import com.alchemy.sdk.AlchemySettings
import com.alchemy.sdk.ResourceUtils.Companion.parseFile
import com.alchemy.sdk.core.model.Address
import com.alchemy.sdk.core.model.Network
import com.alchemy.sdk.nft.model.FloorPrice
import com.alchemy.sdk.nft.model.GetNftsForContractOptions
import com.alchemy.sdk.nft.model.GetNftsForOwnerOptions
import com.alchemy.sdk.nft.model.Nft
import com.alchemy.sdk.nft.model.NftContractMetadata
import com.alchemy.sdk.nft.model.NftContractNftsResponse
import com.alchemy.sdk.nft.model.NftContractOwnersResponse
import com.alchemy.sdk.nft.model.NftOwnersResponse
import com.alchemy.sdk.nft.model.OwnedNftsResponse
import com.alchemy.sdk.util.HexString.Companion.hexString
import kotlinx.coroutines.test.runTest
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldBeGreaterThan
import org.amshove.kluent.shouldBeInstanceOf
import org.amshove.kluent.shouldNotBeEqualTo
import org.junit.Ignore
import org.junit.Test

class NftIntegrationTest {

    private val alchemy = Alchemy.with(
        AlchemySettings(
            apiKey = System.getenv("ALCHEMY_API_TOKEN"),
            network = Network.ETH_MAINNET
        )
    )

    @Test
    fun `nfts for owner with metadata`() = runTest {
        val ownedNftsResponse =
            alchemy.nft.getNftsForOwner(Address.from("0x1188aa75c38e1790be3768508743fbe7b50b2153"))

        val expectedOwnedNft: OwnedNftsResponse.OwnedAlchemyNftsResponse = parseFile("owned_nfts_with_metadata.json")
        ownedNftsResponse.getOrNull() shouldBeEqualTo expectedOwnedNft
    }

    @Test
    fun `nfts for owner without metadata`() = runTest {
        val ownedNftsResponse =
            alchemy.nft.getNftsForOwner(
                Address.from("0x1188aa75c38e1790be3768508743fbe7b50b2153"),
                GetNftsForOwnerOptions(omitMetadata = true)
            )

        val expectedOwnedNft: OwnedNftsResponse.OwnedBaseNftsResponse = parseFile("owned_nfts_without_metadata.json")
        ownedNftsResponse.getOrNull() shouldBeEqualTo expectedOwnedNft
    }

    @Test
    fun `retrieve nft metadata`() = runTest {
        val nft = alchemy.nft.getNftMetadata(
            Address.from("0x4b076f0e07eed3f1007fb1b5c000f7a08d3208e1"),
            1L
        )
        val expectedNft: Nft = parseFile("nft_metadata_test.json")
        nft.getOrThrow() shouldBeEqualTo expectedNft
    }

    @Test
    fun `retrieve nft contract metadata`() = runTest {
        val nft = alchemy.nft.getContractMetadata(
            Address.from("0x4b076f0e07eed3f1007fb1b5c000f7a08d3208e1")
        )
        val expectedNft: NftContractMetadata = parseFile("contract_metadata_test.json")
        nft.getOrThrow() shouldBeEqualTo expectedNft
    }

    @Test
    fun `retrieve nfts for contract without metadata`() = runTest {
        val nftContractNfts = alchemy.nft.getNftsForContract(
            Address.from("0x4b076f0e07eed3f1007fb1b5c000f7a08d3208e1"),
            GetNftsForContractOptions(omitMetadata = true)
        )
        val expectedNftContractNfts: NftContractNftsResponse = parseFile("nft_for_contracts_without_metadata.json")
        nftContractNfts.getOrThrow() shouldBeEqualTo expectedNftContractNfts
    }

    @Test
    fun `retrieve nfts for contract with metadata`() = runTest {
        val nftContractNfts = alchemy.nft.getNftsForContract(
            Address.from("0x4b076f0e07eed3f1007fb1b5c000f7a08d3208e1")
        )
        val expectedNftContractNfts: NftContractNftsResponse = parseFile("nft_for_contracts_with_metadata.json")
        nftContractNfts.getOrThrow() shouldBeEqualTo expectedNftContractNfts
    }

    @Test
    fun `retrieve owners for nft`() = runTest {
        val owners = alchemy.nft.getOwnersForNft(
            Address.from("0x4b076f0e07eed3f1007fb1b5c000f7a08d3208e1"),
            tokenId = 1L
        )
        val expectedOwners: NftOwnersResponse = parseFile("owners_for_nft_test.json")
        owners.getOrThrow() shouldBeEqualTo expectedOwners
    }

    @Test
    fun `retrieve owners for contract`() = runTest {
        val owners = alchemy.nft.getOwnersForContract(
            Address.from("0x4b076f0e07eed3f1007fb1b5c000f7a08d3208e1")
        )
        val expectedOwners: NftContractOwnersResponse = parseFile("owners_for_contract_test.json")
        owners.getOrThrow() shouldBeEqualTo expectedOwners
    }

    @Test
    fun `retrieve spam contracts`() = runTest {
        val spamContractsResult = alchemy.nft.getSpamContracts()
        val spamContracts = spamContractsResult.getOrThrow()
        spamContracts.size shouldBeGreaterThan 0
        spamContracts[0].value shouldBeEqualTo "0x000386e3f7559d9b6a2f5c46b4ad1a9587d59dc3".hexString
    }

    @Test
    fun `check if a contract is a spam`() = runTest {
        alchemy.nft.isSpamContract(
            Address.from("0x4b076f0e07eed3f1007fb1b5c000f7a08d3208e1")
        ).getOrThrow() shouldBeEqualTo false
    }

    @Test
    fun `check ownership of nft`() = runTest {
        alchemy.nft.checkNftOwnership(
            Address.from("0x1188aa75c38e1790be3768508743fbe7b50b2153"),
            listOf(
                Address.from("0x4b076f0e07eed3f1007fb1b5c000f7a08d3208e1"),
                Address.from("0x4b076f0e07eed3f1007fb1b5c000f7a08d3208e1")
            )
        ).getOrThrow() shouldBeEqualTo true
    }

    @Test(expected = IllegalAccessException::class)
    fun `user of the sdk can't access getNFTsForOwnership`() = runTest {
        alchemy.nft.getNftsForOwnership(
            Address.from("0x1188aa75c38e1790be3768508743fbe7b50b2153")
        )
    }

    @Test
    @Ignore("Alchemy is returning a 500 http code")
    fun `retrieve floor price for the collection`() = runTest {
        val floorPriceResponse = alchemy.nft.getFloorPrice(
            Address.from("0x4b076f0e07eed3f1007fb1b5c000f7a08d3208e1")
        )
        val floorPrice = floorPriceResponse.getOrThrow()
        floorPrice.openSea shouldBeInstanceOf FloorPrice.FloorPriceMarketplace::class.java
        floorPrice.looksRare shouldBeInstanceOf FloorPrice.FloorPriceError::class.java
    }

    @Test
    fun `should refresh metadata`() = runTest {
        val refreshNftMetadataResponse = alchemy.nft.refreshNftMetadata(
            Address.from("0xbc4ca0eda7647a8ab7c2061c2e118a18a936f13d"),
            1L
        )
        refreshNftMetadataResponse.getOrThrow() shouldNotBeEqualTo null
    }

    @Test
    fun `should refresh contract`() = runTest {
        val refreshContractResponse = alchemy.nft.refreshContract(
            Address.from("0xbc4ca0eda7647a8ab7c2061c2e118a18a936f13d")
        )
        refreshContractResponse.getOrThrow() shouldNotBeEqualTo null
    }
}