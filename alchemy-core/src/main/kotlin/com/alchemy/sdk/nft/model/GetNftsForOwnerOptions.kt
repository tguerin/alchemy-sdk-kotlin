package com.alchemy.sdk.nft.model

import com.alchemy.sdk.core.model.Address
import com.alchemy.sdk.util.ProxyRetrofitQueryMap
import com.alchemy.sdk.util.QueryMapObject

data class GetNftsForOwnerOptions(
    /**
     * Optional page key from an existing {@link OwnedBaseNftsResponse} or
     * {@link OwnedNftsResponse}to use for pagination.
     */
    val pageKey: String? = null,

    /** Optional list of contract addresses to filter the results by. Limit is 20. */
    val contractAddresses: List<Address.ContractAddress> = emptyList(),

    /**
     * Optional list of filters applied to the query. NFTs that match one or more
     * of these filters are excluded from the response.
     */
    val excludeFilters: List<NftExcludeFilter> = emptyList(),

    /**
     * Sets the total number of NFTs to return in the response. Defaults to 100.
     * Maximum page size is 100.
     */
    val pageSize: Int? = null,

    /** Optional boolean flag to omit NFT metadata. Defaults to `false`. */
    val omitMetadata: Boolean = false,

    /**
     * No set timeout by default - When metadata is requested, this parameter is
     * the timeout (in milliseconds) for the website hosting the metadata to
     * respond. If you want to only access the cache and not live fetch any
     * metadata for cache misses then set this value to 0.
     */
    val tokenUriTimeoutInMs: Int? = null,

    val refreshCache: Boolean? = null
) : QueryMapObject() {

    init {
        val queryData = ProxyRetrofitQueryMap()
        pageKey?.let {
            queryData["pageKey"] = it
        }
        if (contractAddresses.isNotEmpty()) {
            queryData["contractAddresses[]"] = contractAddresses
        }
        if (excludeFilters.isNotEmpty()) {
            queryData["excludeFilters[]"] = excludeFilters.map { it.value }
        }
        pageSize?.let {
            queryData["pageSize"] = it.toString()
        }
        queryData["withMetadata"] = (!omitMetadata).toString()
        tokenUriTimeoutInMs?.let {
            queryData["tokenUriTimeoutInMs"] = it.toString()
        }
        refreshCache?.let {
            queryData["refreshCache"] = true
        }
        this.queryData = queryData
    }
}