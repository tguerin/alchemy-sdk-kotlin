package com.alchemy.sdk.e2e

import com.alchemy.sdk.Alchemy
import com.alchemy.sdk.AlchemySettings
import com.alchemy.sdk.ResourceUtils.Companion.readFile
import com.alchemy.sdk.core.model.Network
import com.alchemy.sdk.ws.model.BlockHead
import com.alchemy.sdk.ws.model.PendingTransaction
import com.alchemy.sdk.ws.model.WebsocketMethod
import com.alchemy.sdk.ws.model.WebsocketStatus
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
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldBeInstanceOf
import org.junit.Test

class WebSocketIntegrationTest {

    private val alchemy = Alchemy.with(AlchemySettings(network = Network.ETH_MAINNET))

    @Test
    fun `should share the flow for the same method call`() = runTest {
        val blockHeadsFirst = mutableListOf<Result<BlockHead>>()
        val blockHeadsSecond = mutableListOf<Result<BlockHead>>()
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
        pendingTransactionResult.getOrThrow() shouldBeInstanceOf PendingTransaction.HashOnly::class.java
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
        pendingTransactionResult.getOrThrow() shouldBeInstanceOf PendingTransaction.FullPendingTransaction::class.java
    }

    @Test
    fun `should call different ws methods without issue`() = runTest {
        val result = awaitAll(
            async {
                val job = launch {
                    // Wait for a real transaction to be emitted or otherwise fake one
                    delay(1_000L)
                    if (isActive) {
                        alchemy.ws.emit(readFile("ws_full_transaction_message.json"))
                    }
                }
                alchemy.ws.on(WebsocketMethod.PendingTransactions())
                    .take(1)
                    .onEach {
                        job.cancel()
                    }
                    .single()
                    .also {
                        job.cancelAndJoin()
                    }
            },
            async {
                alchemy.ws.on(WebsocketMethod.Block).take(1).single()
            }
        )
        result.size shouldBeEqualTo 2
        (result[0] as Result<*>).isSuccess shouldBeEqualTo true
        (result[1] as Result<*>).isSuccess shouldBeEqualTo true
    }

    @Test
    fun `should listen to websocket status`() = runTest {
        val statusList = mutableListOf<WebsocketStatus>()
        alchemy.ws.status
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
        alchemy.ws.status
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
}