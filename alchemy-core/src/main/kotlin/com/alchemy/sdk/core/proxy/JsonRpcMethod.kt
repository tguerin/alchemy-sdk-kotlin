package com.alchemy.sdk.core.proxy

import com.alchemy.sdk.json.rpc.client.JsonRpcClient
import com.alchemy.sdk.json.rpc.client.annotation.JsonRpc
import com.alchemy.sdk.json.rpc.client.annotation.JsonRpcParam
import com.alchemy.sdk.json.rpc.client.generator.IdGenerator
import com.alchemy.sdk.json.rpc.client.model.JsonRpcRequest
import java.lang.reflect.Method
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type
import java.lang.reflect.WildcardType

class JsonRpcMethod<T> private constructor(
    private val idGenerator: IdGenerator,
    private val jsonRpcClient: JsonRpcClient,
    private val jsonRpcMethod: String,
    private val parameters: List<JsonRpcParam>,
    private val parameterConverters: Map<Class<*>, ParameterConverter<Any, Any>>,
    private val returnType: Type
) {
    @Suppress("UNCHECKED_CAST")
    suspend fun invoke(args: Array<Any?>): Result<T> {
        return try {
            val request = JsonRpcRequest(
                id = idGenerator.generateId(),
                method = jsonRpcMethod,
                params = if (args.size == 1 && args[0] is List<*>) {
                    (args[0] as List<*>).map { item -> transformParameter(item as Any) }
                } else {
                    args.filterNotNull().toList().mapIndexed { index, param ->
                        when {
                            parameters[index].useRawValue -> {
                                param
                            }
                            param is List<*> -> {
                                param.map { item -> transformParameter(item as Any) }
                            }
                            else -> transformParameter(param)
                        }

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
                parameters = method.parameters.mapNotNull {
                    it.annotations.filterIsInstance(
                        JsonRpcParam::class.java
                    ).firstOrNull()
                },
                parameterConverters = parameterConverters,
                returnType = resolveReturnType(method)
            )
        }

        @Suppress("UNCHECKED_CAST")
        private fun resolveReturnType(method: Method): Type {
            return (((method.parameters.last().parameterizedType as ParameterizedType).actualTypeArguments[0] as WildcardType).lowerBounds[0] as ParameterizedType).actualTypeArguments[0]
        }
    }
}