package com.alchemy.sdk.core

import com.alchemy.sdk.core.ResourceUtils.Companion.parseFile
import com.alchemy.sdk.core.model.AlchemySettings
import com.alchemy.sdk.core.model.core.Address
import com.alchemy.sdk.core.model.core.Network
import com.alchemy.sdk.core.model.nft.OwnedNftsResponse
import kotlinx.coroutines.test.runTest
import org.amshove.kluent.shouldNotBeEqualTo
import org.junit.Test

class NftIntegrationTest {

    private val alchemy = Alchemy.with(AlchemySettings(network = Network.ETH_MAINNET))

    @Test
    fun getNftsForOwnerWithMetadata() = runTest {
        val ownedNftsResponse =
            alchemy.nft.getNftsForOwner(Address.from("0x1188aa75c38e1790be3768508743fbe7b50b2153"))

        val expectedOwnedNft =
            parseFile("owned_nfts_with_metadata.json", OwnedNftsResponse::class.java)
        ownedNftsResponse.getOrNull() shouldNotBeEqualTo expectedOwnedNft
    }

    @Test
    fun getNftsForOwnerWithoutMetadata() = runTest {
        val ownedNftsResponse =
            alchemy.nft.getNftsForOwner(Address.from("0x1188aa75c38e1790be3768508743fbe7b50b2153"))

        val expectedOwnedNft =
            parseFile("owned_nfts_without_metadata.json", OwnedNftsResponse::class.java)
        ownedNftsResponse.getOrNull() shouldNotBeEqualTo expectedOwnedNft
    }
}