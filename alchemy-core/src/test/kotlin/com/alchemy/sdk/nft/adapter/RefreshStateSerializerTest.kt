package com.alchemy.sdk.nft.adapter

import com.alchemy.sdk.ResourceUtils.Companion.json
import com.alchemy.sdk.nft.model.RefreshState
import kotlinx.serialization.encodeToString
import org.amshove.kluent.shouldBeEqualTo
import org.junit.Test

class RefreshStateSerializerTest {

    @Test
    fun `should encode refresh state base on enum value`() {
        RefreshState.values().forEach { refreshState ->
            json.encodeToString(
                refreshState
            ) shouldBeEqualTo "\"${refreshState.value}\""
        }
    }
}