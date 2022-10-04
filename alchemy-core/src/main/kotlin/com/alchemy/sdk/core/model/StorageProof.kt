package com.alchemy.sdk.core.model

import com.alchemy.sdk.util.HexString

data class StorageProof(
    val key: HexString,
    val proof: List<HexString>,
    val value: HexString
)