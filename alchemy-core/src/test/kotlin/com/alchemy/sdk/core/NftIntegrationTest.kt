package com.alchemy.sdk.core

import com.alchemy.sdk.core.ResourceUtils.Companion.parseFile
import com.alchemy.sdk.core.model.AlchemySettings
import com.alchemy.sdk.core.model.core.Address
import com.alchemy.sdk.core.model.core.Network
import com.alchemy.sdk.core.model.nft.FloorPrice
import com.alchemy.sdk.core.model.nft.GetNftsForContractOptions
import com.alchemy.sdk.core.model.nft.GetNftsForOwnerOptions
import com.alchemy.sdk.core.model.nft.Nft
import com.alchemy.sdk.core.model.nft.NftContractMetadata
import com.alchemy.sdk.core.model.nft.NftContractNftsResponse
import com.alchemy.sdk.core.model.nft.OwnedNftsResponse
import com.alchemy.sdk.core.model.nft.OwnersResponse
import com.alchemy.sdk.core.util.GsonUtil.Companion.nftGson
import com.alchemy.sdk.core.util.HexString.Companion.hexString
import kotlinx.coroutines.test.runTest
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldBeGreaterThan
import org.amshove.kluent.shouldBeInstanceOf
import org.amshove.kluent.shouldNotBeEqualTo
import org.junit.Ignore
import org.junit.Test

class NftIntegrationTest {

    private val alchemy = Alchemy.with(AlchemySettings(network = Network.ETH_MAINNET))

    @Test
    fun `nfts for owner with metadata`() = runTest {
        val ownedNftsResponse =
            alchemy.nft.getNftsForOwner(Address.from("0x1188aa75c38e1790be3768508743fbe7b50b2153"))

        val expectedOwnedNft =
            parseFile("owned_nfts_with_metadata.json", OwnedNftsResponse::class.java, nftGson)
        ownedNftsResponse.getOrNull() shouldBeEqualTo expectedOwnedNft
    }

    @Test
    fun `nfts for owner without metadata`() = runTest {
        val ownedNftsResponse =
            alchemy.nft.getNftsForOwner(
                Address.from("0x1188aa75c38e1790be3768508743fbe7b50b2153"),
                GetNftsForOwnerOptions(omitMetadata = true)
            )

        val expectedOwnedNft =
            parseFile("owned_nfts_without_metadata.json", OwnedNftsResponse::class.java, nftGson)
        ownedNftsResponse.getOrNull() shouldBeEqualTo expectedOwnedNft
    }

    @Test
    fun `retrieve nft metadata`() = runTest {
        val nft = alchemy.nft.getNftMetadata(
            Address.ContractAddress("0x4b076f0e07eed3f1007fb1b5c000f7a08d3208e1".hexString),
            1L
        )
        val expectedNft = parseFile("nft_metadata_test.json", Nft::class.java, nftGson)
        nft.getOrThrow() shouldBeEqualTo expectedNft
    }

    @Test
    fun `retrieve nft contract metadata`() = runTest {
        val nft = alchemy.nft.getContractMetadata(
            Address.ContractAddress("0x4b076f0e07eed3f1007fb1b5c000f7a08d3208e1".hexString)
        )
        val expectedNft =
            parseFile("contract_metadata_test.json", NftContractMetadata::class.java, nftGson)
        nft.getOrThrow() shouldBeEqualTo expectedNft
    }

    @Test
    fun `retrieve nfts for contract without metadata`() = runTest {
        val nftContractNfts = alchemy.nft.getNftsForContract(
            Address.ContractAddress("0x4b076f0e07eed3f1007fb1b5c000f7a08d3208e1".hexString),
            GetNftsForContractOptions(omitMetadata = true)
        )
        val expectedNftContractNfts = parseFile(
            "nft_for_contracts_without_metadata.json",
            NftContractNftsResponse::class.java,
            nftGson
        )
        nftContractNfts.getOrThrow() shouldBeEqualTo expectedNftContractNfts
    }

    @Test
    fun `retrieve nfts for contract with metadata`() = runTest {
        val nftContractNfts = alchemy.nft.getNftsForContract(
            Address.ContractAddress("0x4b076f0e07eed3f1007fb1b5c000f7a08d3208e1".hexString)
        )
        val expectedNftContractNfts = parseFile(
            "nft_for_contracts_with_metadata.json",
            NftContractNftsResponse::class.java,
            nftGson
        )
        nftContractNfts.getOrThrow() shouldBeEqualTo expectedNftContractNfts
    }

    @Test
    fun `retrieve owners for nft`() = runTest {
        val owners = alchemy.nft.getOwnersForNft(
            Address.ContractAddress("0x4b076f0e07eed3f1007fb1b5c000f7a08d3208e1".hexString),
            tokenId = 1L
        )
        val expectedOwners = parseFile(
            "owners_for_nft_test.json",
            OwnersResponse::class.java,
            nftGson
        )
        owners.getOrThrow() shouldBeEqualTo expectedOwners
    }

    @Test
    fun `retrieve owners for contract`() = runTest {
        val owners = alchemy.nft.getOwnersForContract(
            Address.ContractAddress("0x4b076f0e07eed3f1007fb1b5c000f7a08d3208e1".hexString)
        )
        val expectedOwners = parseFile(
            "owners_for_contract_test.json",
            OwnersResponse::class.java,
            nftGson
        )
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
            Address.ContractAddress("0x4b076f0e07eed3f1007fb1b5c000f7a08d3208e1".hexString)
        ).getOrThrow() shouldBeEqualTo false
    }

    @Test
    fun `check ownership of nft`() = runTest {
        alchemy.nft.checkNftOwnership(
            Address.from("0x1188aa75c38e1790be3768508743fbe7b50b2153"),
            listOf(
                Address.ContractAddress("0x4b076f0e07eed3f1007fb1b5c000f7a08d3208e1".hexString),
                Address.ContractAddress("0x4b076f0e07eed3f1007fb1b5c000f7a08d3208e1".hexString)
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
    fun `retrieve floor price for the collection`() = runTest {
        val floorPriceResponse = alchemy.nft.getFloorPrice(
            Address.ContractAddress("0x4b076f0e07eed3f1007fb1b5c000f7a08d3208e1".hexString)
        )
        val floorPrice = floorPriceResponse.getOrThrow()
        floorPrice.openSea shouldBeInstanceOf FloorPrice.FloorPriceMarketplace::class.java
        floorPrice.looksRare shouldBeInstanceOf FloorPrice.FloorPriceError::class.java
    }

    @Test
    fun `should refresh metadata`() = runTest {
        val refreshNftMetadataResponse = alchemy.nft.refreshNftMetadata(
            Address.ContractAddress("0xbc4ca0eda7647a8ab7c2061c2e118a18a936f13d".hexString),
            1L
        )
        refreshNftMetadataResponse.getOrThrow() shouldNotBeEqualTo  null
    }
}