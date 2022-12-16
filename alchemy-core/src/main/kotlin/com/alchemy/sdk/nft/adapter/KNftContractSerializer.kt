package com.alchemy.sdk.nft.adapter

import com.alchemy.sdk.nft.model.NftContract
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.json.JsonContentPolymorphicSerializer
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.jsonObject

object KNftContractSerializer : JsonContentPolymorphicSerializer<NftContract>(NftContract::class) {
    override fun selectDeserializer(element: JsonElement): DeserializationStrategy<out NftContract> {
        return if ("contractMetadata" in element.jsonObject) {
            NftContract.AlchemyNftContract.serializer()
        } else {
            NftContract.BaseNftContract.serializer()
        }
    }

}