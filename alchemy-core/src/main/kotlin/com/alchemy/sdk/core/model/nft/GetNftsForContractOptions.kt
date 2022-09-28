package com.alchemy.sdk.core.model.nft

import com.alchemy.sdk.core.util.ProxyRetrofitQueryMap
import com.alchemy.sdk.core.util.QueryMapObject

data class GetNftsForContractOptions(
    /**
     * Optional page key from an existing {@link OwnedBaseNftsResponse} or
     * {@link OwnedNftsResponse}to use for pagination.
     */
    val pageKey: String? = null,

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
) : QueryMapObject() {

    init {
        val queryData = ProxyRetrofitQueryMap()
        pageKey?.let {
            queryData["pageKey"] = it
        }
        pageSize?.let {
            queryData["pageSize"] = it.toString()
        }
        queryData["withMetadata"] = (!omitMetadata).toString()
        tokenUriTimeoutInMs?.let {
            queryData["tokenUriTimeoutInMs"] = it.toString()
        }
        this.queryData = queryData
    }
}