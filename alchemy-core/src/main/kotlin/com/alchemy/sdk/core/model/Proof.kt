package com.alchemy.sdk.core.model

import com.alchemy.sdk.core.util.HexString
import com.alchemy.sdk.core.util.Wei

data class Proof(
    val address: Address,
    val accountProof: List<HexString>,
    val balance: Wei,
    val codeHash: HexString,
    val nonce: HexString,
    val storageHash: HexString,
    val storageProof: List<StorageProof>
)