package com.alchemy.sdk.util

import com.alchemy.sdk.core.model.Address
import com.alchemy.sdk.core.model.BlockTransaction
import com.alchemy.sdk.nft.model.NftContract
import com.alchemy.sdk.nft.model.NftId
import com.alchemy.sdk.nft.model.NftTokenType
import com.alchemy.sdk.nft.model.OwnedNft
import com.alchemy.sdk.nft.model.OwnedNftsResponse
import com.alchemy.sdk.nft.model.TokenMetadata
import com.alchemy.sdk.util.HexString.Companion.hexString
import com.alchemy.sdk.ws.model.PendingTransaction
import org.amshove.kluent.shouldBeEqualTo
import org.junit.Test

class GsonUtilTest {

    @Test
    fun `should return null address for default creator`() {
        GsonUtil.addressCreator.createInstance(Address::class.java) shouldBeEqualTo Constants.ADDRESS_ZERO
    }

    @Test
    fun `should return block transaction unknown for default creator`() {
        GsonUtil.blockTransactionCreator.createInstance(BlockTransaction::class.java) shouldBeEqualTo BlockTransaction.Unknown
    }

    @Test
    fun `should return pending transaction with null hash for default creator`() {
        GsonUtil.pendingTransactionCreator.createInstance(PendingTransaction::class.java) shouldBeEqualTo PendingTransaction.HashOnly(
            "0x".hexString
        )
    }

    @Test
    fun `should return empty owned base nfts for default creator`() {
        GsonUtil.ownedNftsCreator.createInstance(OwnedNftsResponse::class.java) shouldBeEqualTo OwnedNftsResponse.OwnedBaseNftsResponse(
            ownedNfts = emptyList(),
            pageKey = null,
            totalCount = 0
        )
    }

    @Test
    fun `should return empty owned base nft for default creator`() {
        GsonUtil.ownedNftCreator.createInstance(OwnedNft::class.java) shouldBeEqualTo OwnedNft.OwnedBaseNft(
            0L,
            contract = NftContract.BaseNftContract(Address.ContractAddress("0x0".hexString)),
            id = NftId("0".hexString, TokenMetadata(NftTokenType.Unknown)),
        )
    }

    @Test
    fun `should return empty nft base contract for default creator`() {
        GsonUtil.nftContractCreator.createInstance(NftContract::class.java) shouldBeEqualTo NftContract.BaseNftContract(
            Address.ContractAddress("0x0".hexString)
        )
    }
}