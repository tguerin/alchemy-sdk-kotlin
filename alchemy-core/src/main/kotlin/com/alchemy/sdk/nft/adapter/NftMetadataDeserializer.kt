package com.alchemy.sdk.nft.adapter

import com.alchemy.sdk.nft.model.NftMetadata
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonNull
import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type

object NftMetadataDeserializer : JsonDeserializer<NftMetadata?> {
    override fun deserialize(
        json: JsonElement,
        typeOfT: Type,
        context: JsonDeserializationContext
    ): NftMetadata? {
        return if (json == JsonNull.INSTANCE) {
            null
        } else {
            check(json is JsonObject)
            NftMetadata(
                context.deserialize(json, object : TypeToken<Map<String, Any>>() {}.type)
            )
        }
    }

}