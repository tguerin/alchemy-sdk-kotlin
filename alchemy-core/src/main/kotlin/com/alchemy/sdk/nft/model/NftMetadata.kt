package com.alchemy.sdk.nft.model

import com.alchemy.sdk.nft.adapter.KNftMetadataSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.doubleOrNull
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.longOrNull

@Serializable(with = KNftMetadataSerializer::class)
@Suppress("UNCHECKED_CAST")
data class NftMetadata(
    internal val metadata: Map<String, JsonElement>
) {

    val date: Long?
        get() = (metadata["date"] as JsonPrimitive?)?.longOrNull

    val image: String?
        get() = (metadata["image"] as JsonPrimitive?)?.contentOrNull

    val dna: String?
        get() = (metadata["dna"] as JsonPrimitive?)?.contentOrNull

    val name: String?
        get() = (metadata["name"] as JsonPrimitive?)?.contentOrNull

    val description: String?
        get() = (metadata["description"] as JsonPrimitive?)?.contentOrNull

    val edition: Double?
        get() = (metadata["edition"] as JsonPrimitive?)?.doubleOrNull

    val attributes: List<Map<String, Any>>?
        get() = (metadata["attributes"] as JsonArray?)?.map { it.jsonObject.toMap() }

}