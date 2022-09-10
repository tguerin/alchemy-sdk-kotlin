package com.alchemy.sdk.json.rpc.client

import com.alchemy.sdk.json.rpc.client.model.JsonRpcRequest


interface JsonRpcClient {
    suspend fun <T> call(request: JsonRpcRequest, returnType: Class<T>): Result<T>
}