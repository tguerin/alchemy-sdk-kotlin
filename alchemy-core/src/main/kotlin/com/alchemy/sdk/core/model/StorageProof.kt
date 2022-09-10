package com.alchemy.sdk.core.model

import com.alchemy.sdk.core.util.HexString

data class StorageProof(
    val key: HexString,
    val proof: List<HexString>,
    val value: HexString
)