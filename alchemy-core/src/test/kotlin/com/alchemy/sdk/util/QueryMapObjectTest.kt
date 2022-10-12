package com.alchemy.sdk.util

import org.amshove.kluent.shouldBeEqualTo
import org.junit.Test

class QueryMapObjectTest {

    class QueryMapObjectForTest : QueryMapObject() {
        init {
            queryData["key"] = "value"
        }
    }

    @Test
    fun `should mimic map`() {
        val queryMapObject = QueryMapObjectForTest()

        queryMapObject["key"] shouldBeEqualTo "value"
        queryMapObject.entries.size shouldBeEqualTo 1
        queryMapObject.keys.size shouldBeEqualTo 1
        queryMapObject.values.size shouldBeEqualTo 1
        queryMapObject.containsKey("key") shouldBeEqualTo true
        queryMapObject.containsValue("value") shouldBeEqualTo true
        queryMapObject.isEmpty() shouldBeEqualTo false
    }
}