package com.alchemy.sdk.nft.api

import com.alchemy.sdk.annotations.GET
import com.alchemy.sdk.annotations.Headers
import com.alchemy.sdk.annotations.Query
import com.alchemy.sdk.annotations.QueryMap
import com.alchemy.sdk.core.model.Address
import com.alchemy.sdk.nft.model.FloorPriceResponse
import com.alchemy.sdk.nft.model.GetNftsForContractOptions
import com.alchemy.sdk.nft.model.GetNftsForOwnerOptions
import com.alchemy.sdk.nft.model.Nft
import com.alchemy.sdk.nft.model.NftContractMetadata
import com.alchemy.sdk.nft.model.NftContractNftsResponse
import com.alchemy.sdk.nft.model.NftContractOwnersResponse
import com.alchemy.sdk.nft.model.NftOwnersResponse
import com.alchemy.sdk.nft.model.NftTokenType
import com.alchemy.sdk.nft.model.OwnedNftsResponse
import com.alchemy.sdk.nft.model.RefreshContractResponse

interface NftApi {

    @GET("getNFTs")
    @Headers(
        "Alchemy-Ethers-Sdk-Method: getNftsForOwner"
    )
    suspend fun getNftsForOwner(
        @Query("owner") owner: Address,
        @QueryMap options: GetNftsForOwnerOptions = GetNftsForOwnerOptions()
    ): Result<OwnedNftsResponse>

    @GET("getNFTMetadata")
    @Headers(
        "Alchemy-Ethers-Sdk-Method: getNFTMetadata"
    )
    suspend fun getNftMetadata(
        @Query("contractAddress") contractAddress: Address,
        @Query("tokenId") tokenId: Long,
        @Query("tokenType") tokenType: NftTokenType? = null,
        @Query("tokenUriTimeoutInMs") tokenUriTimeoutInMs: Int? = null
    ): Result<Nft>

    @GET("getNFTMetadata")
    @Headers(
        "Alchemy-Ethers-Sdk-Method: refreshNftMetadata"
    )
    suspend fun getNftMetadataRefreshed(
        @Query("contractAddress") contractAddress: Address,
        @Query("tokenId") tokenId: Long,
        @Query("refreshCache") refreshCache: Boolean = true
    ): Result<Nft>

    @GET("getContractMetadata")
    @Headers(
        "Alchemy-Ethers-Sdk-Method: getContractMetadata"
    )
    suspend fun getContractMetadata(
        @Query("contractAddress") contractAddress: Address
    ): Result<NftContractMetadata>

    @GET("getNFTsForCollection")
    @Headers(
        "Alchemy-Ethers-Sdk-Method: getNFTsForContract"
    )
    suspend fun getNftsForContract(
        @Query("contractAddress") contractAddress: Address,
        @QueryMap options: GetNftsForContractOptions = GetNftsForContractOptions()
    ): Result<NftContractNftsResponse>

    @GET("getOwnersForToken")
    @Headers(
        "Alchemy-Ethers-Sdk-Method: getOwnersForNft"
    )
    suspend fun getOwnersForNft(
        @Query("contractAddress") contractAddress: Address,
        @Query("tokenId") tokenId: Long
    ): Result<NftOwnersResponse>

    @GET("getOwnersForCollection")
    @Headers(
        "Alchemy-Ethers-Sdk-Method: getOwnersForContract"
    )
    suspend fun getOwnersForContract(
        @Query("contractAddress") contractAddress: Address
    ): Result<NftContractOwnersResponse>

    @GET("isSpamContract")
    @Headers(
        "Alchemy-Ethers-Sdk-Method: isSpamContract"
    )
    suspend fun isSpamContract(
        @Query("contractAddress") contractAddress: Address
    ): Result<Boolean>

    @GET("getSpamContracts")
    @Headers(
        "Alchemy-Ethers-Sdk-Method: getSpamContracts"
    )
    suspend fun getSpamContracts(): Result<List<Address>>

    @GET("getNFTs")
    @Headers(
        "Alchemy-Ethers-Sdk-Method: checkNftOwnership"
    )
    suspend fun getNftsForOwnership(
        @Query("owner") owner: Address,
        @QueryMap options: GetNftsForOwnerOptions = GetNftsForOwnerOptions()
    ): Result<OwnedNftsResponse>

    @GET("getFloorPrice")
    @Headers(
        "Alchemy-Ethers-Sdk-Method: getFloorPrice"
    )
    suspend fun getFloorPrice(
        @Query("contractAddress") contractAddress: Address
    ): Result<FloorPriceResponse>

    @GET("reingestContract")
    @Headers(
        "Alchemy-Ethers-Sdk-Method: refreshContract"
    )
    suspend fun refreshContract(
        @Query("contractAddress") contractAddress: Address
    ): Result<RefreshContractResponse>
}