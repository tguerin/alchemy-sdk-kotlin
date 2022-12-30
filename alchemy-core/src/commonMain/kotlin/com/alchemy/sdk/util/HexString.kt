package com.alchemy.sdk.util

import com.alchemy.sdk.core.adapter.KHexStringSerializer
import com.ionspin.kotlin.bignum.decimal.BigDecimal
import com.ionspin.kotlin.bignum.integer.BigInteger
import io.ktor.utils.io.charsets.Charsets
import io.ktor.utils.io.core.toByteArray
import kotlinx.serialization.Serializable
import org.komputing.khash.keccak.Keccak
import org.komputing.khash.keccak.KeccakParameter

@Serializable(with = KHexStringSerializer::class)
class HexString private constructor(val data: String) {

    fun withoutPrefix(): String = data.substring(2)

    fun withoutLeadingZero(): String = data.replace("0x0", "0x")

    fun decimalValue(): BigInteger = BigInteger.parseString(withoutPrefix(), 16)

    fun intValue(): Int = decimalValue().intValue()

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

    fun hasLength(length: Int): Boolean {
        require(length > 0)
        return length() == length
    }

    fun slice(startIndex: Int): HexString {
        val withoutPrefix = withoutPrefix()
        require(startIndex >= 0 && startIndex < withoutPrefix.length * 2) {
            "Invalid start index $startIndex"
        }
        return withoutPrefix.substring(startIndex * 2).hexString
    }

    fun slice(startIndex: Int, endIndex: Int): HexString {
        val withoutPrefix = withoutPrefix()
        require(startIndex >= 0 && startIndex < withoutPrefix.length * 2) {
            "Invalid start index $startIndex"
        }
        require(endIndex >= startIndex && endIndex <= withoutPrefix.length * 2) {
            "Invalid end index $endIndex"
        }
        return withoutPrefix.substring(startIndex * 2, endIndex * 2).hexString
    }

    fun parseString(start: Int): String? {
        try {
            val parseBytes = parseBytes(start)
            return if (parseBytes == null) {
                null
            } else {
                val bytes = parseBytes.toByteArray()
                // TODO don't have a better solution for now
                io.ktor.utils.io.core.String(bytes, 0, bytes.size, Charsets.UTF_8)
            }
        } catch (_: Exception) {
        }
        return null
    }

    fun parseBytes(start: Int): HexString? {
        if (this == "0x".hexString) return null

        val offset = slice(start, start + 32).intValue()
        val length = slice(offset, offset + 32).intValue()

        return slice(offset + 32, offset + 32 + length)
    }

    fun length(): Int {
        return withoutPrefix().length / 2
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as HexString

        if (data != other.data) return false

        return true
    }

    override fun hashCode(): Int {
        return data.hashCode()
    }

    override fun toString(): String {
        return "HexString(data='$data')"
    }

    companion object {
        private val hexRegex = "^(0x)*[0-9a-fA-F]*$".toRegex()

        fun isValidHex(raw: String) = hexRegex.matches(raw)

        val Number.hexString: HexString
            get() {
                return when (this) {
                    is Int -> {
                        val hexRepresentation = toString(16).lowercase()
                        HexString(
                            "0x${if (hexRepresentation.length % 2 != 0) "0" else ""}$hexRepresentation"
                        )
                    }

                    is Long -> {
                        val hexRepresentation = toString(16).lowercase()
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
                    val byteString = eachByte.toUByte().toString(16)
                    if (byteString.length == 1) {
                        "0$byteString"
                    } else {
                        byteString
                    }
                }

                return HexString(
                    if (hexRepresentation.length % 2 != 0) {
                        hexRepresentation.replace("0x", "0x0")
                    } else {
                        hexRepresentation
                    }
                )
            }

        val IntArray.hexString: HexString
            get() {
                val hexRepresentation = joinToString(separator = "", prefix = "0x") { eachInt ->
                    eachInt.toString(16)
                }
                return HexString(
                    if (hexRepresentation.length % 2 != 0) {
                        hexRepresentation.replace("0x", "0x0")
                    } else {
                        hexRepresentation
                    }
                )
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

        val String.id: HexString
            get() {
                return Keccak.digest(this.toByteArray(), KeccakParameter.KECCAK_256).hexString
            }
    }
}