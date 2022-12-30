package com.alchemy.sdk.core.model

import com.alchemy.sdk.util.Ether
import com.alchemy.sdk.util.HexString
import kotlinx.serialization.Serializable

@Serializable
data class Proof(
    val address: Address,
    val accountProof: List<HexString>,
    val balance: Ether,
    val codeHash: HexString,
    val nonce: HexString,
    val storageHash: HexString,
    val storageProof: List<StorageProof>
)