package com.alchemy.sdk.json.rpc.client.annotation

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class JsonRpc(val method: String)
