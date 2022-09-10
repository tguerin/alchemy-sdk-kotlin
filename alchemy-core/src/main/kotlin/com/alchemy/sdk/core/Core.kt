package com.alchemy.sdk.core

import com.alchemy.sdk.core.api.CoreApi

class Core(private val coreApi: CoreApi) : CoreApi by coreApi {

    /* fun getResolver(addressOrName: String): Flow<Result<String>> {
        return jsonRpcClient.call(
            url,
            TransactionCallRequest(
                Transaction(
                    to = "0x00000000000C2E074eC69A0dFb2997BA6C7d2e1e",
                    data = "0x0178b8bf" + Address.from(addressOrName).value.withoutPrefix()
                )
            ),
            String::class.java
        )
    }*/
}