package com.alchemy.sdk.core.util

import com.alchemy.sdk.core.ens.DnsEncoder
import com.alchemy.sdk.core.util.HexString.Companion.hexString
import org.amshove.kluent.shouldBeEqualTo
import org.junit.Test

class DnsEncoderTest {
    @Test
    fun `should mimic ethers dns encode`() {
        DnsEncoder.dnsEncode("1.offchainexample.eth") shouldBeEqualTo "0x01310f6f6666636861696e6578616d706c650365746800".hexString
    }

    @Test(expected = IllegalArgumentException::class)
    fun `should throw eception if component exceed 63`() {
        DnsEncoder.dnsEncode("${"a".repeat(64)}.offchainexample.eth")
    }

    @Test
    fun `should not throw exception if component size is 63`() {
        DnsEncoder.dnsEncode("${"a".repeat(63)}.offchainexample.eth")
    }
}