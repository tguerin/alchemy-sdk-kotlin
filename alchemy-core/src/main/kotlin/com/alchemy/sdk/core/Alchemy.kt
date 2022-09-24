package com.alchemy.sdk.core

import com.alchemy.sdk.core.api.CoreApi
import com.alchemy.sdk.core.api.NftApi
import com.alchemy.sdk.core.model.AlchemySettings
import com.alchemy.sdk.core.proxy.AlchemyProxy
import com.alchemy.sdk.core.util.Constants
import com.alchemy.sdk.core.util.GsonStringConverter
import com.alchemy.sdk.core.util.GsonUtil
import com.alchemy.sdk.core.util.ResultCallAdapter
import com.alchemy.sdk.json.rpc.client.generator.IncrementalIdGenerator
import com.alchemy.sdk.json.rpc.client.http.HttpJsonRpcClient
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.callbackFlow
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


@Suppress("UNCHECKED_CAST")
class Alchemy private constructor(alchemySettings: AlchemySettings) {

    private val gson = GsonUtil.gson

    val core by lazy {
        setupCore(alchemySettings)
    }

    val nft by lazy {
        setupNft(alchemySettings)
    }

    private fun setupCore(alchemySettings: AlchemySettings): Core {
        val alchemyUrl =
            Constants.getAlchemyHttpUrl(alchemySettings.network, alchemySettings.apiKey)
        val alchemyProxy =
            AlchemyProxy(
                idGenerator = IncrementalIdGenerator(),
                jsonRpcClient = HttpJsonRpcClient(
                    alchemyUrl,
                    OkHttpClient(),
                    gson
                )
            )
        return Core(alchemyProxy.createProxy(CoreApi::class.java))
    }

    private fun setupNft(alchemySettings: AlchemySettings): Nft {
        val alchemyUrl =
            Constants.getAlchemyNftUrl(alchemySettings.network, alchemySettings.apiKey)
        val retrofit = Retrofit.Builder()
            .baseUrl(alchemyUrl)
            .addCallAdapterFactory(ResultCallAdapter)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .addConverterFactory(GsonStringConverter(gson))
            .build()
        return Nft(retrofit.create(NftApi::class.java))
    }

    companion object {
        fun with(alchemySettings: AlchemySettings) = Alchemy(alchemySettings)
        fun asyncWith(alchemySettings: AlchemySettings) = callbackFlow<Alchemy> {
            trySendBlocking(Alchemy(alchemySettings))
            channel.close()
        }
    }
}