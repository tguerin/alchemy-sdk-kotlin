package com.alchemy.sdk.core.util

import com.alchemy.sdk.core.util.Constants.ADDRESS_ZERO

object Formatters {

    fun formatCallAddress(value: HexString): String? {
        if (!value.hasLength(32)) return null
        val address = value.slice(12)
        return if (address == ADDRESS_ZERO) null else address.data
    }
}