package com.alchemy.sdk.core.model

import com.alchemy.sdk.util.HexString
import kotlinx.serialization.Serializable

@Serializable
data class StorageProof(
    val key: HexString,
    val proof: List<HexString>,
    val value: HexString
)