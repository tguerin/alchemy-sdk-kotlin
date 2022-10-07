package com.alchemy.sdk.e2e

import com.alchemy.sdk.Alchemy
import com.alchemy.sdk.AlchemySettings
import com.alchemy.sdk.core.model.Address
import com.alchemy.sdk.core.model.Network
import com.alchemy.sdk.ws.model.BlockHead
import com.alchemy.sdk.ws.model.PendingTransaction
import com.alchemy.sdk.ws.model.WebsocketMethod
import com.alchemy.sdk.ws.model.WebsocketStatus
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.single
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldBeInstanceOf
import org.junit.Ignore
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
    @Ignore("Sometime there is no pending transaction")
    fun `should listen to pending transactions hash only`() = runTest {
        val pendingTransactionResult = alchemy.ws.on(
            WebsocketMethod.PendingTransactions(
                hashesOnly = true
            )
        )
            .take(1)
            .single()
        pendingTransactionResult.isSuccess shouldBeEqualTo true
        pendingTransactionResult.getOrThrow() shouldBeInstanceOf PendingTransaction.HashOnly::class.java
    }

    @Test
    @Ignore("Sometime there is no pending transaction")
    fun `should listen to pending transactions`() = runTest {
        val pendingTransactionResult = alchemy.ws.on(WebsocketMethod.PendingTransactions())
            .take(1)
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