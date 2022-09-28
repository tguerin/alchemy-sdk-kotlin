package com.alchemy.sdk.core.api

import com.alchemy.sdk.core.model.core.Address
import com.alchemy.sdk.core.model.nft.*
import com.alchemy.sdk.core.util.HexString
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
        @Query("tokenId") tokenId: HexString,
        @Query("tokenType") tokenType: NftTokenType? = null,
        @Query("tokenUriTimeoutInMs") tokenUriTimeoutInMs: Int? = null,
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
}