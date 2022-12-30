package com.alchemy.sdk.ens

import com.alchemy.sdk.util.Constants
import java.net.IDN

actual object EnsNormalizer {
    actual fun normalize(name: String): String {
        return IDN.toUnicode(name, IDN.USE_STD3_ASCII_RULES or Constants.NONTRANSITIONAL_TO_UNICODE)
    }
}