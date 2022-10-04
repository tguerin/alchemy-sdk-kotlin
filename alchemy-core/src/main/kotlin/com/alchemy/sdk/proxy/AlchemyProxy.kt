package com.alchemy.sdk.proxy

import com.alchemy.sdk.json.rpc.client.generator.IdGenerator
import com.alchemy.sdk.json.rpc.client.http.HttpJsonRpcClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.lang.reflect.Method
import java.lang.reflect.Proxy
import java.util.concurrent.ConcurrentHashMap
import kotlin.coroutines.Continuation
import kotlin.coroutines.resume


internal class AlchemyProxy(
    private val idGenerator: IdGenerator,
    private val jsonRpcClient: HttpJsonRpcClient
) {
    private val jsonRpcMethodCache = ConcurrentHashMap<Method, JsonRpcMethod<Any>>()

    @Suppress("UNCHECKED_CAST")
    fun <T> createProxy(service: Class<T>): T {
        if (!service.isInterface) {
            throw IllegalArgumentException("API must be an interface")
        }
        eagerlyValidateMethods(service)
        return Proxy.newProxyInstance(
            service.classLoader, arrayOf(service)
        ) { _, method, args ->
            val nonNullArgs = args ?: arrayOf()
            val originalContinuation = args.lastOrNull() as Continuation<Any?>
            CoroutineScope(originalContinuation.context).launch(Dispatchers.IO) {
                val argumentsWithoutContinuation = nonNullArgs.take(nonNullArgs.size - 1)
                val result = loadServiceMethod(method)
                    .invoke(argumentsWithoutContinuation.toTypedArray())
                originalContinuation.resume(result)
            }
            kotlin.coroutines.intrinsics.COROUTINE_SUSPENDED
        } as T
    }

    private fun eagerlyValidateMethods(service: Class<*>) {
        for (method in service.declaredMethods) {
            loadServiceMethod(method)
        }
    }

    private fun loadServiceMethod(method: Method): JsonRpcMethod<Any> {
        var result: JsonRpcMethod<Any>? = jsonRpcMethodCache[method]
        if (result != null) return result

        synchronized(jsonRpcMethodCache) {
            result = jsonRpcMethodCache[method]
            if (result == null) {
                result = JsonRpcMethod.parseAnnotations(
                    idGenerator = idGenerator,
                    jsonRpcClient = jsonRpcClient,
                    method = method
                )
                jsonRpcMethodCache[method] = result!!
            }
        }
        return result!!
    }
}