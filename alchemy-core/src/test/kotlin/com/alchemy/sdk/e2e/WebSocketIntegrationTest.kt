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
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.single
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.toList
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
        val pendingTransactionResult = alchemy.ws.on(
            WebsocketMethod.PendingTransactions(
                hashesOnly = true
            )
        )
            .onStart {
                launch {
                    // Wait for a real transaction to be emitted or otherwise fake one
                    delay(500L)
                    alchemy.ws.emit(readFile("ws_hash_only_transaction_message_test.json"))
                }
            }
            .take(1)
            .single()
        pendingTransactionResult.isSuccess shouldBeEqualTo true
        pendingTransactionResult.getOrThrow() shouldBeInstanceOf PendingTransaction.HashOnly::class.java
    }

    @Test
    fun `should listen to pending transactions`() = runTest {
        val pendingTransactionResult = alchemy.ws.on(WebsocketMethod.PendingTransactions())
            .take(1)
            .onStart {
                launch {
                    // Wait for a real transaction to be emitted or otherwise fake one
                    delay(500L)
                    alchemy.ws.emit(readFile("ws_full_transaction_message.json"))
                }
            }
            .single()

        pendingTransactionResult.isSuccess shouldBeEqualTo true
        pendingTransactionResult.getOrThrow() shouldBeInstanceOf PendingTransaction.FullPendingTransaction::class.java
    }

    @Test
    fun `should listen to websocket status`() = runTest {
        val statusList = mutableListOf<WebsocketStatus>()
        alchemy.ws.status.take(2).toList(statusList)
        statusList shouldBeEqualTo listOf(WebsocketStatus.Disconnected, WebsocketStatus.Connected)
    }
}