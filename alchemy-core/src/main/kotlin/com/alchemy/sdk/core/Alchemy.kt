package com.alchemy.sdk.core

import com.alchemy.sdk.core.api.CoreApi
import com.alchemy.sdk.core.model.AlchemySettings
import com.alchemy.sdk.core.proxy.AlchemyProxy
import com.alchemy.sdk.core.util.Constants
import com.alchemy.sdk.core.util.GsonUtil
import com.alchemy.sdk.json.rpc.client.generator.IncrementalIdGenerator
import com.alchemy.sdk.json.rpc.client.http.HttpJsonRpcClient
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.callbackFlow
import okhttp3.OkHttpClient

@Suppress("UNCHECKED_CAST")
class Alchemy private constructor(alchemySettings: AlchemySettings) {

    val core: Core

    init {
        val alchemyUrl =
            Constants.getAlchemyHttpUrl(alchemySettings.network, alchemySettings.apiKey)
        val gson = GsonUtil.get()
        val alchemyProxy =
            AlchemyProxy(
                idGenerator = IncrementalIdGenerator(),
                jsonRpcClient = HttpJsonRpcClient(
                    alchemyUrl,
                    OkHttpClient(),
                    gson
                )
            )
        core = Core(alchemyProxy.createProxy(CoreApi::class.java))
    }

    companion object {
        fun with(alchemySettings: AlchemySettings) = Alchemy(alchemySettings)
        fun asyncWith(alchemySettings: AlchemySettings) = callbackFlow<Alchemy> {
            trySendBlocking(Alchemy(alchemySettings))
            channel.close()
        }
    }
}