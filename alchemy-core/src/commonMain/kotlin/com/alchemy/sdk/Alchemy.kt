package com.alchemy.sdk

import com.alchemy.sdk.ccip.CcipReadFetcher
import com.alchemy.sdk.core.Core
import com.alchemy.sdk.core.api.CoreApiImpl
import com.alchemy.sdk.core.model.Network
import com.alchemy.sdk.ens.DnsEncoder
import com.alchemy.sdk.ens.EnsNormalizer
import com.alchemy.sdk.nft.Nft
import com.alchemy.sdk.nft.api.NftApiImpl
import com.alchemy.sdk.transact.Transact
import com.alchemy.sdk.util.Constants
import com.alchemy.sdk.util.Dispatchers
import com.alchemy.sdk.util.HttpClientEngineProvider.provideHttpClientEngine
import com.alchemy.sdk.util.generator.IncrementalIdGenerator
import com.alchemy.sdk.ws.DelayRetryPolicy
import com.alchemy.sdk.ws.RetryPolicy
import com.alchemy.sdk.ws.WebSocket
import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.websocket.WebSockets
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.lighthousegames.logging.KmLogging
import org.lighthousegames.logging.LogLevel

data class AlchemySettings(
    /** The Alchemy API key that can be found in the Alchemy dashboard. */
    val apiKey: String = Constants.DEFAULT_ALCHEMY_API_KEY,

    /**
     * The name of the network. Once configured, the network cannot be changed. To
     * use a different network, instantiate a new `Alchemy` instance
     */
    val network: Network = Constants.DEFAULT_NETWORK,

    /**
     * Optional URL endpoint to use for all requests. Setting this field will
     * override the URL generated by the {@link network} and {@link apiKey} fields.
     *
     * This field is useful for testing or for using a custom node endpoint. Note
     * that not all methods will work with custom URLs.
     */
    val url: String? = null,

    /**
     * Settings for websocket
     */
    val wsSettings: WsSettings = WsSettings()
) {
    data class WsSettings(
        val retryPolicy: RetryPolicy = DelayRetryPolicy()
    )
}


@Suppress("UNCHECKED_CAST")
class Alchemy private constructor(alchemySettings: AlchemySettings) {

    private val dispatchers = Dispatchers()

    private val idGenerator = IncrementalIdGenerator()

    private val json = Json {
        encodeDefaults = true
        ignoreUnknownKeys = true
    }

    val core by lazy {
        setupCore(alchemySettings)
    }

    val nft by lazy {
        setupNft(alchemySettings)
    }

    // Remove this annotation when transact will be tested
    @Suppress("unused")
    val transact by lazy {
        Transact(core)
    }

    val ws by lazy {
        WebSocket(
            idGenerator = idGenerator,
            core = core,
            json = json,
            websocketUrl = Constants.getAlchemyWebsocketUrl(
                alchemySettings.network,
                alchemySettings.apiKey
            ),
            httpClient = HttpClient(provideHttpClientEngine()) {
                install(WebSockets) {
                    pingInterval = 10_000
                }
            },
            retryPolicy = alchemySettings.wsSettings.retryPolicy,
            dispatchers = dispatchers
        )
    }

    init {
        KmLogging.setLogLevel(LogLevel.Off)
    }

    private fun setupCore(alchemySettings: AlchemySettings): Core {
        val alchemyUrl =
            Constants.getAlchemyHttpUrl(alchemySettings.network, alchemySettings.apiKey)
        val client = HttpClient(provideHttpClientEngine()) {
            install(ContentNegotiation) {
                json(json)
            }
        }
        return Core(
            alchemySettings.network,
            CcipReadFetcher(client),
            CoreApiImpl(
                alchemyUrl,
                idGenerator,
                client,
                json
            ),
            DnsEncoder(EnsNormalizer),
            dispatchers
        )
    }

    private fun setupNft(alchemySettings: AlchemySettings): Nft {
        val alchemyUrl =
            Constants.getAlchemyNftUrl(alchemySettings.network, alchemySettings.apiKey)
        val client = HttpClient(provideHttpClientEngine()) {
            install(ContentNegotiation) {
                json(json)
            }
        }
        return Nft(core, NftApiImpl(alchemyUrl, client, json))
    }

    companion object {
        fun with(alchemySettings: AlchemySettings) = Alchemy(alchemySettings)
    }
}