package com.alchemy.sdk.json.rpc.client.model

class JsonRpcException(val jsonRpcError: JsonRpcError) : RuntimeException()