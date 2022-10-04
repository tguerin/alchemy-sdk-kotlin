package com.alchemy.sdk.core.model

import com.alchemy.sdk.util.HexString
import com.google.gson.annotations.SerializedName

class Validators : ArrayList<Validator>()

data class Validator(
    @SerializedName("ID")
    val id: Long,
    val signer: HexString,
    val power: Int,
    val accum: Int
)