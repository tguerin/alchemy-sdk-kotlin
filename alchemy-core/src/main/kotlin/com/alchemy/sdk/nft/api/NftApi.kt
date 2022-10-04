package com.alchemy.sdk.nft.api

import com.alchemy.sdk.core.model.Address
import com.alchemy.sdk.nft.model.FloorPriceResponse
import com.alchemy.sdk.nft.model.GetNftsForContractOptions
import com.alchemy.sdk.nft.model.GetNftsForOwnerOptions
import com.alchemy.sdk.nft.model.Nft
import com.alchemy.sdk.nft.model.NftContractMetadata
import com.alchemy.sdk.nft.model.NftContractNftsResponse
import com.alchemy.sdk.nft.model.NftTokenType
import com.alchemy.sdk.nft.model.OwnedNftsResponse
import com.alchemy.sdk.nft.model.OwnersResponse
import com.alchemy.sdk.nft.model.RefreshContractResponse
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query
import retrofit2.http.QueryMap

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
        @Query("contractAddress") contractAddress: Address.ContractAddress,
        @Query("tokenId") tokenId: Long,
        @Query("tokenType") tokenType: NftTokenType? = null,
        @Query("tokenUriTimeoutInMs") tokenUriTimeoutInMs: Int? = null
    ): Result<Nft>

    @GET("getNFTMetadata")
    @Headers(
        "Alchemy-Ethers-Sdk-Method: refreshNftMetadata"
    )
    suspend fun getNftMetadataRefreshed(
        @Query("contractAddress") contractAddress: Address.ContractAddress,
        @Query("tokenId") tokenId: Long,
        @Query("refreshCache") refreshCache: Boolean = true
    ): Result<Nft>

    @GET("getContractMetadata")
    @Headers(
        "Alchemy-Ethers-Sdk-Method: getContractMetadata"
    )
    suspend fun getContractMetadata(
        @Query("contractAddress") contractAddress: Address.ContractAddress
    ): Result<NftContractMetadata>

    @GET("getNFTsForCollection")
    @Headers(
        "Alchemy-Ethers-Sdk-Method: getNFTsForContract"
    )
    suspend fun getNftsForContract(
        @Query("contractAddress") contractAddress: Address.ContractAddress,
        @QueryMap options: GetNftsForContractOptions = GetNftsForContractOptions()
    ): Result<NftContractNftsResponse>

    @GET("getOwnersForToken")
    @Headers(
        "Alchemy-Ethers-Sdk-Method: getOwnersForNft"
    )
    suspend fun getOwnersForNft(
        @Query("contractAddress") contractAddress: Address.ContractAddress,
        @Query("tokenId") tokenId: Long
    ): Result<OwnersResponse>

    @GET("getOwnersForCollection")
    @Headers(
        "Alchemy-Ethers-Sdk-Method: getOwnersForContract"
    )
    suspend fun getOwnersForContract(
        @Query("contractAddress") contractAddress: Address.ContractAddress
    ): Result<OwnersResponse>

    @GET("isSpamContract")
    @Headers(
        "Alchemy-Ethers-Sdk-Method: isSpamContract"
    )
    suspend fun isSpamContract(
        @Query("contractAddress") contractAddress: Address.ContractAddress
    ): Result<Boolean>

    @GET("getSpamContracts")
    @Headers(
        "Alchemy-Ethers-Sdk-Method: getSpamContracts"
    )
    suspend fun getSpamContracts(): Result<List<Address.ContractAddress>>

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
        @Query("contractAddress") contractAddress: Address.ContractAddress
    ): Result<FloorPriceResponse>

    @GET("reingestContract")
    @Headers(
        "Alchemy-Ethers-Sdk-Method: refreshContract"
    )
    suspend fun refreshContract(
        @Query("contractAddress") contractAddress: Address.ContractAddress
    ): Result<RefreshContractResponse>
}