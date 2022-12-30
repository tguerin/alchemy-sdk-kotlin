package com.alchemy.sdk.ens

actual object EnsNormalizer {
    actual fun normalize(name: String): String {
        // TODO find a way to normalize url on iOS
        return name
    }
}