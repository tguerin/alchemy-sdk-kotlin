package com.alchemy.sdk.ens

expect object EnsNormalizer {
    fun normalize(name: String): String
}