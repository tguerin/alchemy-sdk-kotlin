package com.alchemy.sdk.nft.model

import com.alchemy.sdk.nft.adapter.KNftExcludeFilterSerializer
import kotlinx.serialization.Serializable

@Serializable(with = KNftExcludeFilterSerializer::class)
enum class NftExcludeFilter(val value: String) {
    Spam("SPAM"),
    Airdrops("AIRDROPS")
}