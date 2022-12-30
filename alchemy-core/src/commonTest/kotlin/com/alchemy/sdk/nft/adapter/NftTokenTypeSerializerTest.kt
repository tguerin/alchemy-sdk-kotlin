package com.alchemy.sdk.nft.adapter

import com.alchemy.sdk.ResourceUtils.Companion.json
import com.alchemy.sdk.nft.model.NftTokenType
import kotlinx.serialization.encodeToString
import com.alchemy.sdk.shouldBeEqualTo
import kotlin.test.Test

class NftTokenTypeSerializerTest {

    @Test
    fun `should serialize token type base on enum value`() {
        NftTokenType.values().forEach { tokenType ->
            json.encodeToString(
                tokenType
            ) shouldBeEqualTo "\"${tokenType.value}\""
        }
    }

}