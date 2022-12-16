package com.alchemy.sdk.nft.adapter

import com.alchemy.sdk.ResourceUtils.Companion.json
import com.alchemy.sdk.nft.model.NftTokenType
import kotlinx.serialization.decodeFromString
import org.amshove.kluent.shouldBeEqualTo
import org.junit.Test

class NftTokenTypeDeserializerTest {

    @Test(expected = Exception::class)
    fun `should throw exception if not a string`() {
        json.decodeFromString<NftTokenType>("1") shouldBeEqualTo NftTokenType.Unknown
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