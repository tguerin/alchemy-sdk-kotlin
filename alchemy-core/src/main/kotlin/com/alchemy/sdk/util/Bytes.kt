package com.alchemy.sdk.util

import kotlin.math.ceil

fun Int.arrayify(): IntArray {
    val result = mutableListOf<Int>()
    var remainingValue = this
    while (remainingValue != 0) {
        result.add(0, remainingValue and 0xff)
        remainingValue /= 256
    }
    if (result.size == 0) {
        result.add(0)
    }
    return result.toIntArray()
}

fun Int.numPad(): IntArray {
    val result = this.arrayify()
    if (result.size > 32) {
        throw IllegalArgumentException("internal; should not happen")
    }
    val padded = IntArray(32)
    padded.set(result, 32 - result.size)
    return padded
}

fun HexString.bytesPad(): IntArray {
    return this.toIntArray().bytesPad()
}

fun IntArray.bytesPad(): IntArray {
    if ((this.size % 32) == 0) return this
    return IntArray(ceil(this.size / 32.0).toInt() * 32).apply {
        set(this@bytesPad)
    }
}

fun List<IntArray>.hexConcat(): HexString {
    return this.map(IntArray::hexConcat).reduce { acc, hexString ->
        acc + hexString
    }
}

fun encodeBytes(vararg data: HexString): HexString {
    val dataSize = data.size
    require(dataSize >= 0) {
        "Data size must be at least 1 was $dataSize"
    }
    val result = mutableListOf<IntArray>()

    var byteCount = 32 * dataSize

    for (i in data.indices) {
        val element = data[i]
        // Update the bytes offset
        result.add(i, byteCount.numPad())

        // The length and padded value of data
        result.add(element.length().numPad())
        result.add(element.bytesPad())
        byteCount += 32 + ceil(element.length() / 32.0).toInt() * 32
    }

    return result.hexConcat()
}