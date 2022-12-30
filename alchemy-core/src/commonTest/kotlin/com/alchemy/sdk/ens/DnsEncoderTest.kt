package com.alchemy.sdk.ens

import com.alchemy.sdk.util.HexString.Companion.hexString
import com.alchemy.sdk.shouldBeEqualTo
import kotlin.test.Test
import kotlin.test.assertFailsWith

class DnsEncoderTest {

    private val dnsEncoder = DnsEncoder(EnsNormalizer)

    @Test
    fun `should mimic ethers dns encode`() {
        dnsEncoder.encode("1.offchainexample.eth") shouldBeEqualTo "0x01310f6f6666636861696e6578616d706c650365746800".hexString
    }

    @Test
    fun `should throw exception if component exceed 63`() {
        assertFailsWith<IllegalArgumentException> {
            dnsEncoder.encode("${"a".repeat(64)}.offchainexample.eth")
        }
    }

    @Test
    fun `should not throw exception if component size is 63`() {
        dnsEncoder.encode("${"a".repeat(63)}.offchainexample.eth")
    }
}