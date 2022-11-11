package com.alchemy.sdk.annotations

@Target(AnnotationTarget.FUNCTION)
annotation class JsonRpc(val method: String)
