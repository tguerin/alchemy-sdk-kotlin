package com.alchemy.sdk.core.util

import com.alchemy.sdk.core.model.core.Address
import com.alchemy.sdk.core.util.HexString.Companion.hexString
import org.amshove.kluent.shouldBeEqualTo
import org.junit.Test

class ProxyRetrofitQueryMapTest {

    @Test
    fun `should duplicate query params if it's a list`() {
        val queryMap = ProxyRetrofitQueryMap().apply {
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
    fun `should let non list value untouched`() {
        val queryMap = ProxyRetrofitQueryMap().apply {
            this["contractAddress"] = Address.ContractAddress("0x4b076f0e07eed3f1007fb1b5c000f7a08d3208e1".hexString)
        }
        val entriesList = queryMap.entries.toList()
        entriesList.size shouldBeEqualTo 1
        entriesList[0].key shouldBeEqualTo "contractAddress"
        entriesList[0].value shouldBeEqualTo Address.ContractAddress("0x4b076f0e07eed3f1007fb1b5c000f7a08d3208e1".hexString)
    }

}