package com.alchemy.sdk.util

import com.alchemy.sdk.core.model.Address
import com.alchemy.sdk.util.HexString.Companion.hexString
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldContainAll
import org.junit.Test

class ProxyQueryMapTest {

    @Test
    fun `should duplicate query params if it's a list`() {
        val queryMap = ProxyQueryMap().apply {
            this["contractAddresses[]"] = listOf(
                Address.ContractAddress("0x4b076f0e07eed3f1007fb1b5c000f7a08d3208e1".hexString),
                Address.ContractAddress("0x4b076f0e07eed3f1007fb1b5c000f7a08d3208e2".hexString)
            )
        }
        val entriesList = queryMap.entries.toList()
        entriesList.size shouldBeEqualTo 2
        entriesList[0].key shouldBeEqualTo "contractAddresses[]"
        entriesList[0].value shouldBeEqualTo Address.ContractAddress("0x4b076f0e07eed3f1007fb1b5c000f7a08d3208e1".hexString)
        entriesList[1].key shouldBeEqualTo "contractAddresses[]"
        entriesList[1].value shouldBeEqualTo Address.ContractAddress("0x4b076f0e07eed3f1007fb1b5c000f7a08d3208e2".hexString)
    }

    @Test
    fun `should mimic a classic map`() {
        val value =
            Address.ContractAddress("0x4b076f0e07eed3f1007fb1b5c000f7a08d3208e1".hexString)
        val key = "contractAddress"
        val queryMap = ProxyQueryMap().apply {
            this[key] = value
        }
        queryMap.keys shouldBeEqualTo setOf(key)
        queryMap.size shouldBeEqualTo 1
        queryMap.values shouldContainAll listOf(value)
        queryMap.containsKey(key) shouldBeEqualTo true
        queryMap.containsValue(value) shouldBeEqualTo true
        queryMap[key] shouldBeEqualTo value
        queryMap.isEmpty() shouldBeEqualTo false
    }

    @Test
    fun `should let non list value untouched`() {
        val queryMap = ProxyQueryMap().apply {
            this["contractAddress"] =
                Address.ContractAddress("0x4b076f0e07eed3f1007fb1b5c000f7a08d3208e1".hexString)
        }
        val entriesList = queryMap.entries.toList()
        entriesList.size shouldBeEqualTo 1
        entriesList[0].key shouldBeEqualTo "contractAddress"
        entriesList[0].value shouldBeEqualTo Address.ContractAddress("0x4b076f0e07eed3f1007fb1b5c000f7a08d3208e1".hexString)
    }

}