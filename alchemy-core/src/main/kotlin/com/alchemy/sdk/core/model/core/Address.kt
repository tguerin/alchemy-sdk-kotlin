package com.alchemy.sdk.core.model.core

import com.alchemy.sdk.core.util.Constants.NONTRANSITIONAL_TO_UNICODE
import com.alchemy.sdk.core.util.HexString
import com.alchemy.sdk.core.util.HexString.Companion.hexString
import org.komputing.khash.keccak.Keccak
import org.komputing.khash.keccak.KeccakParameter
import java.net.IDN

sealed class Address private constructor(
    val value: HexString
) {
    class EthereumAddress internal constructor(value: HexString) : Address(value) {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false
            if (!super.equals(other)) return false
            return true
        }

        override fun hashCode(): Int {
            return value.hashCode()
        }
    }

    class ContractAddress constructor(value: HexString) : Address(value) {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false
            if (!super.equals(other)) return false
            return true
        }

        override fun hashCode(): Int {
            return value.hashCode()
        }
    }

    class EnsAddress constructor(val rawAddress: String, value: HexString) : Address(value) {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false
            if (!super.equals(other)) return false
            return true
        }

        override fun hashCode(): Int {
            return value.hashCode()
        }
    }

    companion object {
        private val checksumRegex = "([A-F].*[a-f])|([a-f].*[A-F])".toRegex()
        fun from(rawAddress: String): Address {
            if (rawAddress.isEmpty()) {
                throw IllegalArgumentException("Address can't be empty")
            }
            return when {
                HexString.isValidHex(rawAddress) -> {
                    val sanitizedAddress = if (!rawAddress.startsWith("0x")) {
                        "0x$rawAddress"
                    } else {
                        rawAddress
                    }
                    val result = getChecksumAddress(rawAddress.hexString)
                    // It is a checksummed address with a bad checksum
                    if (checksumRegex.containsMatchIn(sanitizedAddress) && result != rawAddress) {
                        throw IllegalArgumentException("Bad checksum")
                    }
                    EthereumAddress(result.hexString)
                }
                else -> {
                    val sanitizedAddress = rawAddress.lowercase()
                    val normalizedName = IDN.toUnicode(
                        sanitizedAddress,
                        IDN.USE_STD3_ASCII_RULES or NONTRANSITIONAL_TO_UNICODE
                    )
                    EnsAddress(normalizedName, nameHash(normalizedName))
                }
            }
        }

        private fun getChecksumAddress(address: HexString): String {
            if (!address.hasLength(20)) {
                throw IllegalArgumentException("Invalid length for address expected 40 was ${address.length()}")
            }
            val sanitizedAddress = address.withoutPrefix()
            val chars = sanitizedAddress.toCharArray()
            val expanded = IntArray(40)
            for (i in 0 until 40) {
                expanded[i] = chars[i].code
            }
            val hashed = sha3(expanded.map { it.toByte() }.toByteArray()).toIntArray()
            for (i in 0 until 40 step 2) {
                if ((hashed[i shr 1] shr 4) >= 8) {
                    chars[i] = chars[i].uppercaseChar()
                }
                if ((hashed[i shr 1] and 0x0f) >= 8) {
                    chars[i + 1] = chars[i + 1].uppercaseChar()
                }
            }
            return "0x" + chars.joinToString("")
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