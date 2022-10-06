package com.alchemy.sdk.e2e

import com.alchemy.sdk.Alchemy
import com.alchemy.sdk.AlchemySettings
import com.alchemy.sdk.core.model.Network
import com.alchemy.sdk.ws.dataOnly
import com.alchemy.sdk.ws.model.WebsocketMethod
import com.alchemy.sdk.ws.model.WebsocketStatus
import com.alchemy.sdk.ws.statusOnly
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.amshove.kluent.shouldBeEqualTo
import org.junit.Test

class WsIntegrationTest {

    private val alchemy = Alchemy.with(AlchemySettings(network = Network.ETH_MAINNET))

    @Test
    fun `should listen to websocket block method`() = runTest {
        val expectedBlockNumber = alchemy.core.getBlockNumber().getOrThrow()
        val result = alchemy.ws.on(WebsocketMethod.Block).dataOnly().first()
        result.getOrThrow() shouldBeEqualTo expectedBlockNumber
    }

    @Test
    fun `should listen to websocket status`() = runTest {
        val statusList = mutableListOf<WebsocketStatus>()
        alchemy.ws.on(WebsocketMethod.Block).statusOnly().take(2).toList(statusList)
        statusList shouldBeEqualTo listOf(WebsocketStatus.Disconnected, WebsocketStatus.Connected)
    }
}