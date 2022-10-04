package com.alchemy.sdk.nft.model

import com.alchemy.sdk.util.HexString
import com.alchemy.sdk.util.ProxyRetrofitQueryMap
import com.alchemy.sdk.util.QueryMapObject

data class GetNftsForContractOptions(
    /**
     * Optional  An offset used for pagination.
     */
    val startToken: HexString? = null,

    /**
     * Sets the total number of NFTs to return in the response. Defaults to 100.
     * Maximum limit size is 100.
     */
    val limit: Int? = null,

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
        startToken?.let {
            // Somehow this parameter requires a 64 long hex (+2 for 0x)
            var startTokenAsString = it.toString()
            val missingDigits = 66 - startTokenAsString.length
            if (missingDigits > 0) {
                startTokenAsString =
                    startTokenAsString.replace("0x", "0x" + "0".repeat(missingDigits))
            }
            queryData["startToken"] = startTokenAsString
        }
        limit?.let {
            queryData["limit"] = it.toString()
        }
        queryData["withMetadata"] = (!omitMetadata).toString()
        tokenUriTimeoutInMs?.let {
            queryData["tokenUriTimeoutInMs"] = it.toString()
        }
        this.queryData = queryData
    }
}