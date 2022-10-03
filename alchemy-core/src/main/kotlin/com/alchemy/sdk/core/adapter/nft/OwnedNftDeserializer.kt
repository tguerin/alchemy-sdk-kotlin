package com.alchemy.sdk.core.adapter.nft

import com.alchemy.sdk.core.model.nft.Media
import com.alchemy.sdk.core.model.nft.Nft
import com.alchemy.sdk.core.model.nft.NftContract
import com.alchemy.sdk.core.model.nft.NftContractMetadata
import com.alchemy.sdk.core.model.nft.NftId
import com.alchemy.sdk.core.model.nft.NftMetadata
import com.alchemy.sdk.core.model.nft.OwnedNft
import com.alchemy.sdk.core.model.nft.TokenUri
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonNull
import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type

object OwnedNftDeserializer : JsonDeserializer<OwnedNft?> {
    override fun deserialize(
        json: JsonElement,
        typeOfT: Type,
        context: JsonDeserializationContext
    ): OwnedNft? {
        return when {
            json == JsonNull.INSTANCE -> null
            json is JsonObject && (typeOfT == OwnedNft.OwnedAlchemyNft::class.java || isAlchemyNft(
                json
            )) -> deserializeAlchemyNft(json, context)
            typeOfT == OwnedNft.OwnedBaseNft::class.java || json is JsonObject -> context.deserialize(
                json,
                OwnedNft.OwnedBaseNft::class.java
            )
            else -> throw IllegalStateException("Unknown Nft type")
        }
    }

    private fun deserializeAlchemyNft(
        json: JsonObject,
        context: JsonDeserializationContext
    ): OwnedNft {
        val baseContract = context.deserialize<NftContract.BaseNftContract>(
            json.get("contract"),
            NftContract.BaseNftContract::class.java
        )
        val contractMetadata = json.get("contractMetadata")
        val alchemyContract = if (contractMetadata == JsonNull.INSTANCE) {
            NftContract.AlchemyNftContract(baseContract.address)
        } else {
            NftContract.AlchemyNftContract(
                baseContract.address,
                context.deserialize(json.get("contractMetadata"), NftContractMetadata::class.java),
            )
        }

        return OwnedNft.OwnedAlchemyNft(
            json.get("balance").asLong,
            alchemyContract,
            context.deserialize(json.get("id"), NftId::class.java),
            context.deserialize(json.get("title"), String::class.java),
            context.deserialize(json.get("description"), String::class.java),
            context.deserialize(json.get("timeLastUpdated"), String::class.java),
            context.deserialize(json.get("metadataError"), String::class.java),
            context.deserialize(json.get("metadata"), NftMetadata::class.java),
            context.deserialize(json.get("tokenUri"), TokenUri::class.java),
            context.deserialize(json.get("media"), object : TypeToken<List<Media>>() {}.type),
        )
    }

    private fun isAlchemyNft(json: JsonObject): Boolean {
        return Nft.alchemyNftSpecificPropertyNames.any { property ->
            json.has(property)
        }
    }
}