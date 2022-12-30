package com.alchemy.sdk.nft.adapter

import com.alchemy.sdk.ResourceUtils.Companion.json
import com.alchemy.sdk.nft.model.NftMetadata
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.JsonPrimitive
import com.alchemy.sdk.shouldBeEqualTo
import kotlin.test.Test

class NftMetadataSerializerTest {

    @Test
    fun `should serialize metadata`() {
        val nftMetadata = NftMetadata(mapOf("date" to JsonPrimitive(12345)))
        json.encodeToString(nftMetadata) shouldBeEqualTo "{\"date\":12345}"
    }

}