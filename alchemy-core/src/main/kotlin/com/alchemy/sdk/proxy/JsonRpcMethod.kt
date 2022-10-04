package com.alchemy.sdk.proxy

import com.alchemy.sdk.json.rpc.client.JsonRpcClient
import com.alchemy.sdk.json.rpc.client.annotation.JsonRpc
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
    private val returnType: Type
) {
    @Suppress("UNCHECKED_CAST")
    suspend fun invoke(args: Array<Any?>): Result<T> {
        return try {
            val request = JsonRpcRequest(
                id = idGenerator.generateId(),
                method = jsonRpcMethod,
                params = args.toList()
            )
            jsonRpcClient.call(request, returnType)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    companion object {

        fun <T> parseAnnotations(
            idGenerator: IdGenerator,
            jsonRpcClient: JsonRpcClient,
            method: Method
        ): JsonRpcMethod<T> {
            val jsonRpcAnnotation = method.annotations.filterIsInstance(JsonRpc::class.java).first()
            return JsonRpcMethod(
                idGenerator = idGenerator,
                jsonRpcClient = jsonRpcClient,
                jsonRpcMethod = jsonRpcAnnotation.method,
                returnType = resolveReturnType(method)
            )
        }

        @Suppress("UNCHECKED_CAST")
        private fun resolveReturnType(method: Method): Type {
            return (((method.parameters.last().parameterizedType as ParameterizedType).actualTypeArguments[0] as WildcardType).lowerBounds[0] as ParameterizedType).actualTypeArguments[0]
        }
    }
}