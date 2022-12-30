package com.alchemy.sdk.nft

import com.alchemy.sdk.core.Core
import com.alchemy.sdk.core.model.Address
import com.alchemy.sdk.nft.api.NftApi
import com.alchemy.sdk.nft.model.GetNftsForOwnerOptions
import com.alchemy.sdk.nft.model.Nft
import com.alchemy.sdk.nft.model.Nft.AlchemyNft
import com.alchemy.sdk.nft.model.OwnedNftsResponse
import com.alchemy.sdk.nft.model.RefreshNftMetadataResponse
import com.alchemy.sdk.util.SdkResult
import com.alchemy.sdk.util.getOrThrow

class Nft(
    private val core: Core,
    private val nftApi: NftApi
) : NftApi by nftApi {

    override suspend fun getNftsForOwner(
        owner: Address,
        options: GetNftsForOwnerOptions
    ): SdkResult<OwnedNftsResponse> {
        return core.resolveAddress(owner) {
            nftApi.getNftsForOwner(this, options)
        }
    }

    override suspend fun getNftsForOwnership(
        owner: Address,
        options: GetNftsForOwnerOptions
    ): SdkResult<OwnedNftsResponse> {
        error("Use checkNftOwnership instead")
    }

    suspend fun checkNftOwnership(
        owner: Address,
        contractAddresses: List<Address>
    ): SdkResult<Boolean> {
        return core.resolveAddress(owner) {
            val nftsResult = nftApi.getNftsForOwnership(
                this,
                GetNftsForOwnerOptions(
                    contractAddresses = contractAddresses,
                    omitMetadata = true
                )
            )
            if (nftsResult.isFailure) {
                SdkResult.failure(nftsResult.exceptionOrNull()!!)
            } else {
                SdkResult.success(nftsResult.getOrThrow().ownedNfts.isNotEmpty())
            }
        }
    }

    override suspend fun getNftMetadataRefreshed(
        contractAddress: Address,
        tokenId: Long,
        refreshCache: Boolean
    ): SdkResult<Nft> {
        error("Use refreshNftMetadata instead")
    }

    suspend fun refreshNftMetadata(
        contractAddress: Address,
        tokenId: Long,
    ): SdkResult<RefreshNftMetadataResponse> {
        val nftResult = getNftMetadata(contractAddress, tokenId)
        if (nftResult.isFailure) {
            return SdkResult.failure(nftResult.exceptionOrNull()!!)
        }
        val refreshedNftResult = nftApi.getNftMetadataRefreshed(contractAddress, tokenId)
        if (refreshedNftResult.isFailure) {
            return SdkResult.failure(refreshedNftResult.exceptionOrNull()!!)
        }
        val refreshedNft = refreshedNftResult.getOrThrow() as AlchemyNft
        val nft = nftResult.getOrThrow() as AlchemyNft
        return SdkResult.success(
            RefreshNftMetadataResponse(
                updated = nft.timeLastUpdated != refreshedNft.timeLastUpdated,
                nft = refreshedNft
            )
        )
    }
}