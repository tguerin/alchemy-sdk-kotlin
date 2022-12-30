package com.alchemy.sdk.nft.adapter

import com.alchemy.sdk.ResourceUtils.Companion.json
import com.alchemy.sdk.nft.model.RefreshState
import kotlinx.serialization.decodeFromString
import com.alchemy.sdk.shouldBeEqualTo
import kotlin.test.Test
import kotlin.test.assertFailsWith

class RefreshStateDeserializerTest {

    @Test
    fun `should throw exception if not a string`() {
        assertFailsWith<Exception> {
            json.decodeFromString<RefreshState>("3")
        }
    }

    @Test
    fun `should parse refresh state base on enum value`() {
        RefreshState.values().forEach { refreshState ->
            json.decodeFromString<RefreshState>(
                "\"${refreshState.value}\""
            ) shouldBeEqualTo refreshState
        }
    }
}