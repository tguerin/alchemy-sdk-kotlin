package com.alchemy.sdk.core.util

import com.alchemy.sdk.core.util.HexString.Companion.hexString
import java.math.BigDecimal
import java.math.BigInteger

class Ether private constructor(private val weiHexValue: HexString) {
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
        private val KILO_FACTOR = BigDecimal.valueOf(1_000L)
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
                return Ether((BigDecimal.valueOf(this) * ETHER_FACTOR).hexString)
            }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

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