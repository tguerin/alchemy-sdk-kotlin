package com.alchemy.sdk.nft.adapter

import com.alchemy.sdk.nft.model.NftTokenType
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonNull
import java.lang.reflect.Type

object NftTokenTypeDeserializer : JsonDeserializer<NftTokenType> {
    override fun deserialize(
        json: JsonElement,
        typeOfT: Type,
        context: JsonDeserializationContext
    ): NftTokenType {
        return if (json == JsonNull.INSTANCE) {
            NftTokenType.Unknown
        } else {
            NftTokenType.values().firstOrNull { it.value == json.asString } ?: NftTokenType.Unknown
        }
    }

}