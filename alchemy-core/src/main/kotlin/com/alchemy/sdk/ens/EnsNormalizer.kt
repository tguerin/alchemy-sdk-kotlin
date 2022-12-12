package com.alchemy.sdk.ens

fun interface EnsNormalizer {
    fun normalize(name: String): String
}