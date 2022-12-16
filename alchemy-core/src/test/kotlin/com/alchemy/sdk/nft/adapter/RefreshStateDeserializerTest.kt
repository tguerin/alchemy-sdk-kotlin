package com.alchemy.sdk.nft.adapter

import com.alchemy.sdk.ResourceUtils.Companion.json
import com.alchemy.sdk.nft.model.RefreshState
import kotlinx.serialization.decodeFromString
import org.amshove.kluent.shouldBeEqualTo
import org.junit.Test

class RefreshStateDeserializerTest {

    @Test(expected = Exception::class)
    fun `should throw exception if not a string`() {
        json.decodeFromString<RefreshState>("3") shouldBeEqualTo RefreshState.Unknown
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