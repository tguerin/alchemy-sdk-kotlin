package com.alchemy.sdk.core.model.core

import com.alchemy.sdk.core.util.Ether
import com.alchemy.sdk.core.util.HexString

data class Proof(
    val address: Address,
    val accountProof: List<HexString>,
    val balance: Ether,
    val codeHash: HexString,
    val nonce: HexString,
    val storageHash: HexString,
    val storageProof: List<StorageProof>
)