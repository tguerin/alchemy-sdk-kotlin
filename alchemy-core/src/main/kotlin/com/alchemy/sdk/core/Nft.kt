package com.alchemy.sdk.core

import com.alchemy.sdk.core.api.NftApi
import com.alchemy.sdk.core.model.core.Address
import com.alchemy.sdk.core.model.nft.GetNftsForOwnerOptions
import com.alchemy.sdk.core.model.nft.Nft
import com.alchemy.sdk.core.model.nft.OwnedNftsResponse
import com.alchemy.sdk.core.model.nft.RefreshNftMetadataResponse

class Nft(private val nftApi: NftApi) : NftApi by nftApi {

    override suspend fun getNftsForOwnership(
        owner: Address,
        options: GetNftsForOwnerOptions
    ): Result<OwnedNftsResponse> {
        throw IllegalAccessException("Use checkNftOwnership instead")
    }

    suspend fun checkNftOwnership(
        owner: Address,
        contractAddresses: List<Address.ContractAddress>
    ): Result<Boolean> {
        val nftsResult = nftApi.getNftsForOwnership(
            owner,
            GetNftsForOwnerOptions(
                contractAddresses = contractAddresses,
                omitMetadata = true
            )
        )
        return if (nftsResult.isFailure) {
            Result.failure(nftsResult.exceptionOrNull()!!)
        } else {
            Result.success(nftsResult.getOrThrow().ownedNfts.isNotEmpty())
        }
    }

    override suspend fun getNftMetadataRefreshed(
        contractAddress: Address.ContractAddress,
        tokenId: Long,
        refreshCache: Boolean
    ): Result<Nft> {
        throw IllegalAccessException("Use refreshNftMetadata instead")
    }

    suspend fun refreshNftMetadata(
        contractAddress: Address.ContractAddress,
        tokenId: Long,
    ): Result<RefreshNftMetadataResponse> {
        val nftResult = getNftMetadata(contractAddress, tokenId)
        if (nftResult.isFailure) {
            return Result.failure(nftResult.exceptionOrNull()!!)
        }
        val refreshedNftResult = nftApi.getNftMetadataRefreshed(contractAddress, tokenId)
        if (refreshedNftResult.isFailure) {
            return Result.failure(refreshedNftResult.exceptionOrNull()!!)
        }
        val refreshedNft = refreshedNftResult.getOrThrow() as Nft.AlchemyNft
        val nft = nftResult.getOrThrow() as Nft.AlchemyNft
        return Result.success(
            RefreshNftMetadataResponse(
                updated = nft.timeLastUpdated != refreshedNft.timeLastUpdated,
                nft = refreshedNft
            )
        )
    }
}