package com.alchemy.sdk.ens

import com.alchemy.sdk.util.HexString
import com.alchemy.sdk.util.HexString.Companion.hexString
import com.alchemy.sdk.util.set

class DnsEncoder(
    private val ensNormalizer: EnsNormalizer
) {
    fun encode(rawAddress: String): HexString {
        return ensNameSplit(rawAddress).map { component ->
            // DNS does not allow components over 63 bytes in length
            if (component.size > 63) {
                throw IllegalArgumentException("invalid DNS encoded entry length exceeds 63 bytes")
            }
            val bytes = IntArray(component.size + 1)
            bytes.set(component, 1)
            bytes[0] = bytes.size - 1
            bytes.hexString
        }.reduce { acc, i -> acc + i } + "0x0".hexString
    }

    private fun ensNameSplit(name: String): List<IntArray> {
        val bytes = ensNormalizer.normalize(name).toByteArray().map { it.toInt() }.toIntArray()
        val comps: MutableList<IntArray> = mutableListOf()

        if (name.isEmpty()) return comps

        var last = 0
        for (i in bytes.indices) {
            val d = bytes[i]

            // A separator (i.e. ".") copy this component
            if (d == (0x2e).toInt()) {
                comps.add(checkComponent(bytes.slice(last until i).toIntArray()))
                last = i + 1
            }
        }

        // There was a stray separator at the end of the name
        if (last >= bytes.size) {
            throw IllegalArgumentException("invalid ENS name empty component")
        }

        comps.add(checkComponent(bytes.slice(last until bytes.size).toIntArray()))
        return comps
    }

    private fun checkComponent(comp: IntArray): IntArray {
        if (comp.isEmpty()) error("invalid ENS name empty component")
        return comp
    }
}