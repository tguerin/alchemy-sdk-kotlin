package com.alchemy.sdk.nft.adapter

import com.alchemy.sdk.ResourceUtils.Companion.json
import com.alchemy.sdk.nft.model.NftMetadata
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.JsonPrimitive
import com.alchemy.sdk.shouldBeEqualTo
import kotlin.test.Test

class NftMetadataDeserializerTest {

    @Test
    fun `should return metadata from parsed json object`() {
        val jsonData = "{\"date\":12345,\"image\":\"image.png\",\"dna\":\"dna\",\"name\":\"name\",\"description\":\"description\",\"edition\":1,\"attributes\":[{\"value\":\"The Legend Valley\",\"trait_type\":\"Land\"}]}"
        val metadata = json.decodeFromString<NftMetadata?>(jsonData)
        metadata?.date shouldBeEqualTo 12345L
        metadata?.image shouldBeEqualTo "image.png"
        metadata?.dna shouldBeEqualTo "dna"
        metadata?.name shouldBeEqualTo "name"
        metadata?.description shouldBeEqualTo "description"
        metadata?.edition shouldBeEqualTo 1.0
        metadata?.attributes?.get(0)?.get("value") shouldBeEqualTo JsonPrimitive("The Legend Valley")
        metadata?.attributes?.get(0)?.get("trait_type") shouldBeEqualTo JsonPrimitive("Land")
    }

    @Test
    fun `should handle null case`() {
        val data = json.decodeFromString<NftMetadata?>("null")
        data shouldBeEqualTo null
    }
}