package com.alchemy.sdk.core.util

import java.math.BigInteger

class HexString private constructor(val data: String) {

    fun withoutPrefix(): String = data.substring(2)

    fun decimalValue(): BigInteger = BigInteger.valueOf(java.lang.Long.decode(data))

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

        fun from(dataInt: Int): HexString {
            val hexRepresentation = Integer.toHexString(dataInt).lowercase()
            return HexString(
                "0x${if (hexRepresentation.length % 2 != 0) "0" else ""}$hexRepresentation"
            )
        }

        fun from(byteArray: ByteArray): HexString {
            val hexRepresentation =
                byteArray.joinToString(separator = "", prefix = "0x") { eachByte ->
                    "%02x".format(eachByte)
                }
            return HexString(hexRepresentation)
        }

        fun from(dataStr: String) = if (dataStr.isNotEmpty() && isValidHex(dataStr)) {
            HexString(
                if (!dataStr.startsWith("0x")) {
                    "0x" + dataStr.lowercase()
                } else {
                    dataStr.lowercase()
                }
            )
        } else {
            throw IllegalArgumentException("No a valid hexadecimal string")
        }
    }
}