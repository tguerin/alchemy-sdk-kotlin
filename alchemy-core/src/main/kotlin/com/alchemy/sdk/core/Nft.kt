package com.alchemy.sdk.core

import com.alchemy.sdk.core.api.NftApi
import com.alchemy.sdk.core.model.core.Address
import com.alchemy.sdk.core.model.nft.GetNftsForOwnerOptions
import com.alchemy.sdk.core.model.nft.OwnedNftsResponse

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
}