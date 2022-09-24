package com.alchemy.sdk.core.model.core

import com.alchemy.sdk.core.util.HexString
import com.alchemy.sdk.core.util.HexString.Companion.hexString
import org.komputing.khash.keccak.Keccak
import org.komputing.khash.keccak.KeccakParameter

sealed class Address private constructor(
    val value: HexString
) {
    class EthereumAddress internal constructor(value: HexString) : Address(value)
    class ContractAddress constructor(value: HexString) : Address(value)

    companion object {
        private val checksumRegex = "([A-F].*[a-f])|([a-f].*[A-F])".toRegex()
        fun from(rawAddress: String): Address {
            if (rawAddress.isEmpty()) {
                throw IllegalArgumentException("Address can't be empty")
            }
            return when {
                HexString.isValidHex(rawAddress) -> {
                    val sanitizedAddress = rawAddress.hexString
                    val result = getChecksumAddress(sanitizedAddress)
                    // It is a checksummed address with a bad checksum
                    if (checksumRegex.matches(rawAddress) && result != sanitizedAddress) {
                        throw IllegalArgumentException("Bad checksum")
                    }
                    EthereumAddress(result)
                }
                else -> throw IllegalArgumentException("Unknown address type $rawAddress")
            }
        }

        private fun getChecksumAddress(address: HexString): HexString {
            val addressLength = address.withoutPrefix().length
            if (addressLength != 40) {
                throw IllegalArgumentException("Invalid length for address expected 40 was $addressLength")
            }
            val sanitizedAddress = address.withoutPrefix()
            val chars = sanitizedAddress.toCharArray()
            val expanded = IntArray(40)
            for (i in 0 until 40) {
                expanded[i] = chars[i].code
            }
            val hashed =
                sha3(expanded.map { it.toByte() }.toByteArray()).toByteArray()
                    .map { it.toInt() }
                    .toIntArray()
            for (i in 0 until 40 step 2) {
                if ((hashed[i shr 1] shr 4) >= 8) {
                    chars[i] = chars[i].uppercaseChar()
                }
                if ((hashed[i shr 1] and 0x0f) >= 8) {
                    chars[i + 1] = chars[i + 1].uppercaseChar()
                }
            }
            return chars.joinToString("").hexString
        }

        private fun nameHash(dnsName: String): HexString {
            var node = ByteArray(32) { 0 }.hexString
            val labels = dnsName.split('.')
            for (i in labels.size - 1 downTo 0) {
                node = sha3((node + sha3(labels[i].toByteArray())).toByteArray())
            }
            return node
        }

        private fun sha3(data: ByteArray): HexString {
            return Keccak.digest(data, KeccakParameter.KECCAK_256).hexString
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Address

        if (value != other.value) return false

        return true
    }

    override fun hashCode(): Int {
        return value.hashCode()
    }

}