package com.alchemy.sdk.util

import com.alchemy.sdk.util.HexString.Companion.hexString

fun IntArray.set(other: IntArray, offset: Int = 0): IntArray {
    require(this.size >= other.size) {
        "The input array can't have a greater size"
    }
    require(offset < this.size) {
        "Offset can't be greater than the input array"
    }
    for (i in other.indices) {
        this[offset + i] = other[i]
    }
    return this
}

fun IntArray.hexConcat(): HexString {
    if(isEmpty()) return "0x".hexString
    return this.map { value ->
        if (value in 0..255) {
            value.hexString
        } else {
            throw  IllegalArgumentException("Invalid value for hex concat: $value")
        }
    }.reduce { acc, hexString ->
        acc + hexString
    }
}