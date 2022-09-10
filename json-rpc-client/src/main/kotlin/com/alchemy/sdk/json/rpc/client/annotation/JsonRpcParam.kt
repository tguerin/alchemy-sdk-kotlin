package com.alchemy.sdk.json.rpc.client.annotation

@Target(AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.RUNTIME)
annotation class JsonRpcParam(val paramName: String, val position: Int)
