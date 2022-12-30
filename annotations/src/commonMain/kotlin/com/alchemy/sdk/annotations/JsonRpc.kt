package com.alchemy.sdk.annotations

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.SOURCE)
annotation class JsonRpc(val method: String)
