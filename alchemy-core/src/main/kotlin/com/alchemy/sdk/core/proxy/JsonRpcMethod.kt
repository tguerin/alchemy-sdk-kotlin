package com.alchemy.sdk.core.proxy

import com.alchemy.sdk.json.rpc.client.JsonRpcClient
import com.alchemy.sdk.json.rpc.client.annotation.JsonRpc
import com.alchemy.sdk.json.rpc.client.generator.IdGenerator
import com.alchemy.sdk.json.rpc.client.model.JsonRpcRequest
import java.lang.reflect.Method
import java.lang.reflect.ParameterizedType
import java.lang.reflect.WildcardType

class JsonRpcMethod<T> private constructor(
    private val idGenerator: IdGenerator,
    private val jsonRpcClient: JsonRpcClient,
    private val jsonRpcMethod: String,
    private val parameterConverters: Map<Class<*>, ParameterConverter<Any, Any>>,
    private val returnType: Class<T>
) {
    @Suppress("UNCHECKED_CAST")
    suspend fun invoke(args: Array<Any>): Result<T> {
        return try {
            val request = JsonRpcRequest(
                id = idGenerator.generateId(),
                method = jsonRpcMethod,
                params = args.toList().map {
                    if (it is List<*>) {
                        it.map { item -> transformParameter(item as Any) }
                    } else {
                        transformParameter(it)
                    }
                }
            )
            jsonRpcClient.call(request, returnType)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private suspend fun transformParameter(it: Any): Any {
        var clazz = it::class.java
        while (clazz != Object::class.java && parameterConverters[clazz] == null) {
            clazz = clazz.superclass
        }
        return parameterConverters[clazz]?.convert(it) ?: it
    }

    companion object {
        @Suppress("UNCHECKED_CAST")
        fun <T> parseAnnotations(
            idGenerator: IdGenerator,
            jsonRpcClient: JsonRpcClient,
            parameterConverters: Map<Class<*>, ParameterConverter<Any, Any>>,
            method: Method
        ): JsonRpcMethod<T> {
            val jsonRpcAnnotation = method.annotations.filterIsInstance(JsonRpc::class.java).first()
            return JsonRpcMethod(
                idGenerator = idGenerator,
                jsonRpcClient = jsonRpcClient,
                jsonRpcMethod = jsonRpcAnnotation.method,
                parameterConverters = parameterConverters,
                returnType = (((method.parameters.last().parameterizedType as ParameterizedType).actualTypeArguments[0] as WildcardType).lowerBounds[0] as ParameterizedType).actualTypeArguments[0] as Class<T>
            )
        }
    }
}