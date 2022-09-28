package com.alchemy.sdk.core

import com.alchemy.sdk.core.api.CoreApi
import com.alchemy.sdk.core.api.NftApi
import com.alchemy.sdk.core.model.AlchemySettings
import com.alchemy.sdk.core.proxy.AlchemyProxy
import com.alchemy.sdk.core.util.*
import com.alchemy.sdk.core.util.GsonUtil.Companion.nftGson
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

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(AlchemyVersionInterceptor)
        .build()

    val core by lazy {
        setupCore(alchemySettings)
    }

    val nft by lazy {
        setupNft(alchemySettings)
    }

    val transact by lazy {
        Transact(core)
    }

    private fun setupCore(alchemySettings: AlchemySettings): Core {
        val alchemyUrl =
            Constants.getAlchemyHttpUrl(alchemySettings.network, alchemySettings.apiKey)
        val alchemyProxy =
            AlchemyProxy(
                idGenerator = IncrementalIdGenerator(),
                jsonRpcClient = HttpJsonRpcClient(
                    alchemyUrl,
                    okHttpClient,
                    gson
                )
            )
        return Core(alchemyProxy.createProxy(CoreApi::class.java))
    }

    private fun setupNft(alchemySettings: AlchemySettings): Nft {
        val alchemyUrl =
            Constants.getAlchemyNftUrl(alchemySettings.network, alchemySettings.apiKey)
        val retrofit = Retrofit.Builder()
            .client(okHttpClient)
            .baseUrl(alchemyUrl)
            .addCallAdapterFactory(ResultCallAdapter)
            .addConverterFactory(GsonConverterFactory.create(nftGson))
            .addConverterFactory(GsonStringConverter(nftGson))
            .build()
        return Nft(retrofit.create(NftApi::class.java))
    }

    companion object {
        fun with(alchemySettings: AlchemySettings) = Alchemy(alchemySettings)
    }
}