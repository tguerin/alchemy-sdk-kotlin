package com.alchemy.sdk.core.model.core

import com.alchemy.sdk.core.util.HexString
import com.google.gson.annotations.SerializedName

class Validators : ArrayList<Validator>()

data class Validator(
    @SerializedName("ID")
    val id: Long,
    val signer: HexString,
    val power: Int,
    val accum: Int
)