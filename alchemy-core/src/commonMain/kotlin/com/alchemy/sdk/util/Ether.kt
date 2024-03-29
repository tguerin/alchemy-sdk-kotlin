package com.alchemy.sdk.util

import com.alchemy.sdk.core.adapter.KEtherSerializer
import com.alchemy.sdk.util.HexString.Companion.hexString
import com.ionspin.kotlin.bignum.decimal.BigDecimal
import com.ionspin.kotlin.bignum.integer.BigInteger
import kotlinx.serialization.Serializable

@Serializable(with = KEtherSerializer::class)
class Ether private constructor(val weiHexValue: HexString) {
    private val weiValue = weiHexValue.decimalValue()

    val wei: BigInteger = weiValue

    val kiloWei: BigDecimal
        get() = weiValue.toBigDecimal().divide(KILO_FACTOR)

    val megaWei: BigDecimal
        get() = weiValue.toBigDecimal().divide(MEGA_FACTOR)

    val gigaWei: BigDecimal
        get() = weiValue.toBigDecimal().divide(GIGA_FACTOR)

    val microEther: BigDecimal
        get() = weiValue.toBigDecimal().divide(MICRO_ETHER_FACTOR)

    val milliEther: BigDecimal
        get() = weiValue.toBigDecimal().divide(MILLI_ETHER_FACTOR)

    val ether: BigDecimal
        get() = weiValue.toBigDecimal().divide(ETHER_FACTOR)

    companion object {
        private val KILO_FACTOR = BigDecimal.fromLong(1_000L)
        private val MEGA_FACTOR = KILO_FACTOR * KILO_FACTOR
        private val GIGA_FACTOR = MEGA_FACTOR * KILO_FACTOR
        private val MICRO_ETHER_FACTOR = GIGA_FACTOR * KILO_FACTOR
        private val MILLI_ETHER_FACTOR = MICRO_ETHER_FACTOR * KILO_FACTOR
        private val ETHER_FACTOR = MILLI_ETHER_FACTOR * KILO_FACTOR

        val HexString.wei: Ether
            get() {
                return Ether(this)
            }

        val String.wei: Ether
            get() {
                return hexString.wei
            }

        val Number.wei: Ether
            get() {
                return hexString.wei
            }

        val HexString.ether: Ether
            get() {
                return Ether((decimalValue().toBigDecimal() * ETHER_FACTOR).hexString)
            }

        val String.ether: Ether
            get() {
                return hexString.ether
            }

        val Number.ether: Ether
            get() {
                return hexString.ether
            }

        val Double.ether: Ether
            get() {
                return Ether((BigDecimal.fromDouble(this) * ETHER_FACTOR).hexString)
            }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as Ether

        if (weiHexValue != other.weiHexValue) return false

        return true
    }

    override fun hashCode(): Int {
        return weiHexValue.hashCode()
    }

    override fun toString(): String {
        return weiHexValue.toString()
    }

}