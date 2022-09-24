package com.alchemy.sdk.core.adapter.nft

import com.alchemy.sdk.core.model.nft.Nft
import com.alchemy.sdk.core.model.nft.OwnedNft
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonNull
import com.google.gson.JsonObject
import java.lang.reflect.Type

object OwnedNftDeserializer : JsonDeserializer<OwnedNft?> {
    override fun deserialize(
        json: JsonElement,
        typeOfT: Type,
        context: JsonDeserializationContext
    ): OwnedNft? {
        return when {
            json == JsonNull.INSTANCE -> null
            typeOfT == OwnedNft.OwnedAlchemyNft::class.java || (json is JsonObject && isAlchemyNft(
                json
            )) -> context.deserialize(
                json,
                OwnedNft.OwnedAlchemyNft::class.java
            )
            typeOfT == OwnedNft.OwnedBaseNft::class.java || json is JsonObject -> context.deserialize(
                json,
                OwnedNft.OwnedBaseNft::class.java
            )
            else -> throw IllegalStateException("Unknown Nft type")
        }
    }

    private fun isAlchemyNft(json: JsonObject): Boolean {
        return Nft.alchemyNftSpecificPropertyNames.any { property ->
            json.has(property)
        }
    }
}