package com.alchemy.sdk.json.rpc.client

import com.alchemy.sdk.json.rpc.client.model.JsonRpcRequest
import java.lang.reflect.Type


interface JsonRpcClient {
    suspend fun <T> call(request: JsonRpcRequest, returnType: Type): Result<T>
}