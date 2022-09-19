package com.alchemy.sdk.core.util

import java.math.BigInteger

class HexString private constructor(val data: String) {

    fun withoutPrefix(): String = data.substring(2)

    fun withoutLeadingZero(): String = data.replace("0x0", "0x")

    fun decimalValue(): BigInteger = BigInteger(withoutPrefix(), 16)

    fun intValue(): Int = decimalValue().toInt()

    fun toByteArray(): ByteArray {
        return withoutPrefix().chunked(2)
            .map { it.toInt(16).toByte() }
            .toByteArray()
    }

    operator fun plus(anotherHexString: HexString) = concat(anotherHexString)

    private fun concat(anotherHexString: HexString): HexString {
        return HexString("0x" + withoutPrefix() + anotherHexString.withoutPrefix())
    }

    override fun toString(): String {
        return data
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as HexString

        if (data != other.data) return false

        return true
    }

    override fun hashCode(): Int {
        return data.hashCode()
    }

    companion object {
        private val hexRegex = "^(0x)*[0-9a-fA-F]*$".toRegex()

        fun isValidHex(raw: String) = hexRegex.matches(raw)

        fun from(data: Int): HexString {
            val hexRepresentation = Integer.toHexString(data).lowercase()
            return HexString(
                "0x${if (hexRepresentation.length % 2 != 0) "0" else ""}$hexRepresentation"
            )
        }

        fun from(data: ByteArray): HexString {
            val hexRepresentation =
                data.joinToString(separator = "", prefix = "0x") { eachByte ->
                    "%02x".format(eachByte)
                }
            return HexString(hexRepresentation)
        }

        fun from(data: String) = if (data.isNotEmpty() && isValidHex(data)) {
            HexString(
                if (!data.startsWith("0x")) {
                    "0x" + data.lowercase()
                } else {
                    data.lowercase()
                }
            )
        } else {
            throw IllegalArgumentException("No a valid hexadecimal string")
        }
    }
}