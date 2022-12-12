package com.alchemy.sdk.ens

import com.alchemy.sdk.util.HexString.Companion.hexString
import org.amshove.kluent.shouldBeEqualTo
import org.junit.Test

class DnsEncoderTest {

    private val dnsEncoder =  DnsEncoder(IDNNormalizer)
    @Test
    fun `should mimic ethers dns encode`() {
        dnsEncoder.encode("1.offchainexample.eth") shouldBeEqualTo "0x01310f6f6666636861696e6578616d706c650365746800".hexString
    }

    @Test(expected = IllegalArgumentException::class)
    fun `should throw exception if component exceed 63`() {
        dnsEncoder.encode("${"a".repeat(64)}.offchainexample.eth")
    }

    @Test
    fun `should not throw exception if component size is 63`() {
        dnsEncoder.encode("${"a".repeat(63)}.offchainexample.eth")
    }
}