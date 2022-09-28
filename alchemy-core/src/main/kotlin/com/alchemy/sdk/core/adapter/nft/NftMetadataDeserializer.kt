package com.alchemy.sdk.core.adapter.nft

import com.alchemy.sdk.core.model.nft.NftMetadata
import com.google.gson.*
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