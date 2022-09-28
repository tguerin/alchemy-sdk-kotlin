package com.alchemy.sdk.core.adapter.nft

import com.alchemy.sdk.core.model.nft.NftContract
import com.google.gson.*
import java.lang.reflect.Type

object NftContractDeserializer : JsonDeserializer<NftContract?> {
    override fun deserialize(
        json: JsonElement,
        typeOfT: Type,
        context: JsonDeserializationContext
    ): NftContract? {
        return when {
            json == JsonNull.INSTANCE -> null
            typeOfT == NftContract.AlchemyNftContract::class.java || (json is JsonObject && isAlchemyNftContract(
                json
            )) -> context.deserialize(
                json,
                NftContract.AlchemyNftContract::class.java
            )
            typeOfT == NftContract.BaseNftContract::class.java || json is JsonObject -> context.deserialize(
                json,
                NftContract.BaseNftContract::class.java
            )
            else -> throw IllegalStateException("Unknown Nft type")
        }
    }

    private fun isAlchemyNftContract(json: JsonObject): Boolean {
        return NftContract.alchemyNftSpecificPropertyNames.any { property ->
            json.has(property)
        }
    }
}