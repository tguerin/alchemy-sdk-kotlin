package com.alchemy.sdk.rpc.model

class JsonRpcException(val jsonRpcError: JsonRpcError) : RuntimeException()