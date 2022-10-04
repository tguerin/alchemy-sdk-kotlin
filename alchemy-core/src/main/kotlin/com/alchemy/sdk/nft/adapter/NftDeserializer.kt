package com.alchemy.sdk.nft.adapter

import com.alchemy.sdk.nft.model.Media
import com.alchemy.sdk.nft.model.Nft
import com.alchemy.sdk.nft.model.NftContract
import com.alchemy.sdk.nft.model.NftContractMetadata
import com.alchemy.sdk.nft.model.NftId
import com.alchemy.sdk.nft.model.NftMetadata
import com.alchemy.sdk.nft.model.TokenUri
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonNull
import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type

object NftDeserializer : JsonDeserializer<Nft?> {
    override fun deserialize(
        json: JsonElement,
        typeOfT: Type,
        context: JsonDeserializationContext
    ): Nft? {
        return when {
            json == JsonNull.INSTANCE -> null
            json is JsonObject && (typeOfT == Nft.AlchemyNft::class.java || isAlchemyNft(
                json
            )) -> deserializeAlchemyNft(json, context)
            typeOfT == Nft.BaseNft::class.java || json is JsonObject -> context.deserialize(
                json,
                Nft.BaseNft::class.java
            )
            else -> throw IllegalStateException("Unknown Nft type")
        }
    }

    private fun deserializeAlchemyNft(
        json: JsonObject,
        context: JsonDeserializationContext
    ): Nft {
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

        return Nft.AlchemyNft(
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