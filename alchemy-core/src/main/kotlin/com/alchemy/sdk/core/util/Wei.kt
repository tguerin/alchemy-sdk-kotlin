package com.alchemy.sdk.core.util

import java.math.BigDecimal
import java.math.BigInteger

// TODO not a huge fan of this implem, i would prefer an enum based
data class Wei(private val hexValue: HexString) {
    private val weiValue = hexValue.decimalValue()

    fun toWei(): BigInteger = weiValue
    fun toKiloWei(): BigDecimal = weiValue.toBigDecimal().divide(BigDecimal.valueOf(KILO_FACTOR))
    fun toMegaWei(): BigDecimal = weiValue.toBigDecimal().divide(BigDecimal.valueOf(MEGA_FACTOR))
    fun toGigaWei(): BigDecimal = weiValue.toBigDecimal().divide(BigDecimal.valueOf(GIGA_FACTOR))
    fun toMicroEther(): BigDecimal = weiValue.toBigDecimal().divide(BigDecimal.valueOf(TERA_FACTOR))
    fun toMilliEther(): BigDecimal = weiValue.toBigDecimal().divide(BigDecimal.valueOf(PETA_FACTOR))
    fun toEther(): BigDecimal = weiValue.toBigDecimal().divide(BigDecimal.valueOf(EXA_FACTOR))

    companion object {
        private const val KILO_FACTOR = 1_000L
        private const val MEGA_FACTOR = KILO_FACTOR * KILO_FACTOR
        private const val GIGA_FACTOR = MEGA_FACTOR * KILO_FACTOR
        private const val TERA_FACTOR = GIGA_FACTOR * KILO_FACTOR
        private const val PETA_FACTOR = TERA_FACTOR * KILO_FACTOR
        private const val EXA_FACTOR = PETA_FACTOR * KILO_FACTOR
    }
}