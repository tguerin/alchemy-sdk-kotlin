package com.alchemy.sdk.nft.model

import kotlinx.serialization.json.Json

interface QueryMapEncoder {

    fun encode(json: Json): Map<String, String>
}