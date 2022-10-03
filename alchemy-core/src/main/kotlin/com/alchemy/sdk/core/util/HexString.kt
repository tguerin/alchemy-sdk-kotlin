package com.alchemy.sdk.core.util

import java.math.BigDecimal
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

    fun toIntArray(): IntArray {
        return withoutPrefix().chunked(2)
            .map { it.toInt(16) }
            .toIntArray()
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

    fun hasLength(length: Int): Boolean {
        require(length > 0)
        return length() == length
    }

    fun slice(offset: Int): HexString {
        require(offset > 0)
        return withoutPrefix().substring(offset * 2).hexString
    }

    fun length(): Int {
        return withoutPrefix().length / 2
    }

    companion object {
        private val hexRegex = "^(0x)*[0-9a-fA-F]*$".toRegex()

        fun isValidHex(raw: String) = hexRegex.matches(raw)

        val Number.hexString: HexString
            get() {
                return when (this) {
                    is Int -> {
                        val hexRepresentation = Integer.toHexString(this).lowercase()
                        HexString(
                            "0x${if (hexRepresentation.length % 2 != 0) "0" else ""}$hexRepresentation"
                        )
                    }
                    is Long -> {
                        val hexRepresentation = java.lang.Long.toHexString(this).lowercase()
                        HexString(
                            "0x${if (hexRepresentation.length % 2 != 0) "0" else ""}$hexRepresentation"
                        )
                    }
                    else -> throw IllegalArgumentException("Only Int or Long are supported for now")
                }
            }

        val BigInteger.hexString: HexString
            get() {
                return this.toString(16).hexString
            }

        val BigDecimal.hexString: HexString
            get() {
                // For now we take only the integer part
                return this.toBigInteger().hexString
            }

        val ByteArray.hexString: HexString
            get() {
                val hexRepresentation = joinToString(separator = "", prefix = "0x") { eachByte ->
                    "%02x".format(eachByte)
                }
                return HexString(hexRepresentation)
            }

        val String.hexString: HexString
            get() {
                return if (isNotEmpty() && isValidHex(this)) {
                    val leadingZeroOrEmpty = if (this.length % 2 != 0) "0" else ""
                    HexString(
                        when {
                            !startsWith("0x") -> {
                                "0x$leadingZeroOrEmpty${lowercase()}"
                            }
                            leadingZeroOrEmpty.isNotEmpty() -> {
                                lowercase().replace("0x", "0x0")
                            }
                            else -> lowercase()
                        }
                    )
                } else {
                    throw IllegalArgumentException("No a valid hexadecimal string")
                }
            }
    }
}