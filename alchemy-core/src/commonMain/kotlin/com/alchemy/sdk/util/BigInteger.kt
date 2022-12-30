package com.alchemy.sdk.util

import com.ionspin.kotlin.bignum.decimal.BigDecimal
import com.ionspin.kotlin.bignum.integer.BigInteger

fun BigInteger.toBigDecimal() = BigDecimal.fromBigInteger(this)