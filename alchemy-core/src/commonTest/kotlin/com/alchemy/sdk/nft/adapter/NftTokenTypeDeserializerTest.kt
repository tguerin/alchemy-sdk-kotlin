package com.alchemy.sdk.nft.adapter

import com.alchemy.sdk.ResourceUtils.Companion.json
import com.alchemy.sdk.nft.model.NftTokenType
import kotlinx.serialization.decodeFromString
import com.alchemy.sdk.shouldBeEqualTo
import kotlin.test.Test
import kotlin.test.assertFailsWith

class NftTokenTypeDeserializerTest {

    @Test
    fun `should throw exception if not a string`() {
        assertFailsWith<Exception> {
            json.decodeFromString<NftTokenType>("1") shouldBeEqualTo NftTokenType.Unknown
        }
    }

    @Test
    fun `should parse token type base on enum value`() {
        NftTokenType.values().forEach { tokenType ->
            json.decodeFromString<NftTokenType>(
                "\"${tokenType.value}\""
            ) shouldBeEqualTo tokenType
        }
    }

}