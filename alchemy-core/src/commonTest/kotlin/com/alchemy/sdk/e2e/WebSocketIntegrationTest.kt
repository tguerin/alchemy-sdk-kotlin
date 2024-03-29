package com.alchemy.sdk.e2e

import com.alchemy.sdk.Alchemy
import com.alchemy.sdk.AlchemySettings
import com.alchemy.sdk.ResourceUtils.Companion.json
import com.alchemy.sdk.ResourceUtils.Companion.parseFile
import com.alchemy.sdk.ResourceUtils.Companion.readFile
import com.alchemy.sdk.core.api.CoreApi
import com.alchemy.sdk.core.model.Address
import com.alchemy.sdk.core.model.Network
import com.alchemy.sdk.core.model.TransactionReceipt
import com.alchemy.sdk.shouldBeEqualTo
import com.alchemy.sdk.shouldContainAll
import com.alchemy.sdk.util.Constants
import com.alchemy.sdk.util.Dispatchers
import com.alchemy.sdk.util.HexString.Companion.hexString
import com.alchemy.sdk.util.HexString.Companion.id
import com.alchemy.sdk.util.HttpClientEngineProvider.provideHttpClientEngine
import com.alchemy.sdk.util.SdkResult
import com.alchemy.sdk.util.System
import com.alchemy.sdk.util.generator.IncrementalIdGenerator
import com.alchemy.sdk.util.getOrThrow
import com.alchemy.sdk.ws.DelayRetryPolicy
import com.alchemy.sdk.ws.WebSocket
import com.alchemy.sdk.ws.model.BlockHead
import com.alchemy.sdk.ws.model.PendingTransaction
import com.alchemy.sdk.ws.model.WebsocketMethod
import com.alchemy.sdk.ws.model.WebsocketStatus
import io.ktor.client.HttpClient
import io.ktor.client.plugins.websocket.WebSockets
import io.mockative.Mock
import io.mockative.classOf
import io.mockative.given
import io.mockative.mock
import io.mockative.oneOf
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.single
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

class WebSocketIntegrationTest {

    @Mock
    val core = mock(classOf<CoreApi>())

    private val alchemy = Alchemy.with(
        AlchemySettings(
            apiKey = System.getenv("ALCHEMY_API_TOKEN") ?: error("An api key must be set"),
            network = Network.ETH_MAINNET
        )
    )

    @Test
    fun `should share the flow for the same method call`() = runTest {
        val blockHeadsFirst = mutableListOf<SdkResult<BlockHead>>()
        val blockHeadsSecond = mutableListOf<SdkResult<BlockHead>>()
        val firstJob = async {
            alchemy.ws.on(WebsocketMethod.Block).take(2).toList(blockHeadsFirst)
        }
        val secondJob = async {
            alchemy.ws.on(WebsocketMethod.Block).take(2).toList(blockHeadsSecond)
        }
        awaitAll(firstJob, secondJob)
        blockHeadsFirst shouldBeEqualTo blockHeadsSecond
    }

    @Test
    fun `should listen to pending transactions hash only`() = runTest {
        val job = launch {
            // Wait for a real transaction to be emitted or otherwise fake one
            delay(1_000L)
            if (isActive) {
                alchemy.ws.emit(readFile("ws_hash_only_transaction_message_test.json"))
            }
        }
        val pendingTransactionResult = alchemy.ws.on(
            WebsocketMethod.PendingTransactions(
                hashesOnly = true
            )
        )
            .take(1)
            .single()
        job.cancelAndJoin()
        pendingTransactionResult.isSuccess shouldBeEqualTo true
        pendingTransactionResult.getOrThrow()::class shouldBeEqualTo PendingTransaction.HashOnly::class
    }

    @Test
    fun `should listen to pending transactions`() = runTest {
        val job = launch {
            // Wait for a real transaction to be emitted or otherwise fake one
            delay(1_000L)
            if (isActive) {
                alchemy.ws.emit(readFile("ws_full_transaction_message.json"))
            }
        }
        val pendingTransactionResult = alchemy.ws.on(WebsocketMethod.PendingTransactions())
            .take(1)
            .onEach {
                job.cancel()
            }
            .single()
        job.cancelAndJoin()
        pendingTransactionResult.isSuccess shouldBeEqualTo true
        pendingTransactionResult.getOrThrow()::class shouldBeEqualTo PendingTransaction.FullPendingTransaction::class
    }

    @Test
    fun `should call different ws methods without issue`() = runTest {
        val job = launch {
            // Wait for a real transaction to be emitted or otherwise fake one
            delay(1_000L)
            if (isActive) {
                alchemy.ws.emit(readFile("ws_full_transaction_message.json"))
            }
        }
        val result = awaitAll(
            async {
                alchemy.ws.on(WebsocketMethod.PendingTransactions())
                    .take(1)
                    .single()
            },
            async {
                alchemy.ws.on(WebsocketMethod.Block).take(1).single()
            }
        )
        job.cancelAndJoin()
        result.size shouldBeEqualTo 2
        (result[0] as SdkResult<*>).isSuccess shouldBeEqualTo true
        (result[1] as SdkResult<*>).isSuccess shouldBeEqualTo true
    }

    @Test
    fun `should listen to websocket status`() = runTest {
        val statusList = mutableListOf<WebsocketStatus>()
        alchemy.ws.statusFlow
            .onStart {
                alchemy.ws.connect()
            }
            .take(2)
            .toList(statusList)
        statusList shouldBeEqualTo listOf(WebsocketStatus.Disconnected, WebsocketStatus.Connected)
    }

    @Test
    fun `should reconnect if not a normal close code`() = runTest {
        val statusList = mutableListOf<WebsocketStatus>()
        alchemy.ws.statusFlow
            .onStart {
                alchemy.ws.connect()
            }
            .onEach {
                if (it == WebsocketStatus.Connected) {
                    alchemy.ws.close(4000, "error")
                }
            }
            .take(5)
            .toList(statusList)
        statusList shouldBeEqualTo listOf(
            WebsocketStatus.Disconnected,
            WebsocketStatus.Connected,
            WebsocketStatus.Disconnected,
            WebsocketStatus.Reconnecting,
            WebsocketStatus.Connected
        )
    }

    @Test
    fun `should retrieve directly the transaction receipt if already mined`() = runTest {
        val result = alchemy.ws
            .on(WebsocketMethod.Transaction("0x6576804cb20d1bab7898d22eaf4fed6fec75ddaf43ef43b97f2c8011e449deef".hexString))
            .single()
        result.getOrThrow() shouldBeEqualTo parseFile<TransactionReceipt>("transaction_receipt_test.json")
    }

    @Test
    fun `should retrieve the transaction receipt when a block number is emitted`() = runTest {
        val expectedReceipt: TransactionReceipt = parseFile("transaction_receipt_test.json")
        given(core).suspendFunction(core::getTransactionReceipt)
            .whenInvokedWith(oneOf("0x6576804cb20d1bab7898d22eaf4fed6fec75ddaf43ef43b97f2c8011e449deef".hexString))
            .thenReturn(SdkResult.success(expectedReceipt))
        val ws = WebSocket(
            IncrementalIdGenerator(),
            core,
            json,
            Dispatchers(),
            DelayRetryPolicy(),
            Constants.getAlchemyWebsocketUrl(
                Network.ETH_MAINNET,
                Constants.DEFAULT_ALCHEMY_API_KEY
            ),
            HttpClient(provideHttpClientEngine()) {
                install(WebSockets) {
                    pingInterval = 10_000
                }
            },
        )
        val result = ws
            .on(WebsocketMethod.Transaction("0x6576804cb20d1bab7898d22eaf4fed6fec75ddaf43ef43b97f2c8011e449deef".hexString))
            .take(1)
            .single()
        result.getOrThrow() shouldBeEqualTo expectedReceipt
    }

    @Test
    fun `should get log corresponding to filter`() = runTest {
        val topics = listOf("Transfer(address,address,uint256)".id)
        val logResult = alchemy.ws.on(
            WebsocketMethod.LogFilter(
                address = Address.from("dai.tokens.ethers.eth"),
                topics = topics,
            )
        )
            .take(1)
            .single()
        logResult.isSuccess shouldBeEqualTo true
        val log = logResult.getOrThrow()
        log.address shouldBeEqualTo Address.from("0x6b175474e89094c44da98b954eedeac495271d0f")
        log.topics shouldContainAll  topics
    }
}