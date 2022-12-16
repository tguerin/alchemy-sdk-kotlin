package com.alchemy.sdk.core.model

import com.alchemy.sdk.util.HexString
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Validator(
    @SerialName("ID")
    val id: Long,
    val signer: HexString,
    val power: Int,
    val accum: Int
)