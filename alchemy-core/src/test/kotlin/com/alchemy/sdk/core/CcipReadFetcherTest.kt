package com.alchemy.sdk.core

import com.alchemy.sdk.core.ccip.CcipReadFetcher
import com.alchemy.sdk.core.model.core.Network
import com.alchemy.sdk.core.model.core.TransactionCall
import com.alchemy.sdk.core.util.GsonUtil
import com.alchemy.sdk.core.util.HexString.Companion.hexString
import kotlinx.coroutines.test.runTest
import okhttp3.OkHttpClient
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.amshove.kluent.shouldBeEqualTo
import org.junit.Before
import org.junit.Test

class CcipReadFetcherTest {

    private val okHttpClient = OkHttpClient()

    private val gson = GsonUtil.gson

    lateinit var ccipReadFetcher: CcipReadFetcher

    private val mockWebServer = MockWebServer()

    @Before
    fun setUp() {
        mockWebServer.start()
        ccipReadFetcher = CcipReadFetcher(okHttpClient, gson)
    }

    @Test
    fun `should get data from ccip read url`() = runTest {
        mockWebServer.enqueue(MockResponse().setBody("{\"data\":\"0x3529b5834ea3c6\"}"))
        val result = ccipReadFetcher.fetchCcipRead(
            transactionCall = TransactionCall(
                to = Network.ETH_MAINNET.ensAddress!!,
                data = "0x02".hexString
            ),
            calldata = "0x02".hexString,
            urls = listOf(
                mockWebServer.url("/").toString() + "{sender}/{data}"
            )
        )

        result shouldBeEqualTo "0x3529b5834ea3c6".hexString
    }

    @Test
    fun `validate request params`() = runTest {
        mockWebServer.enqueue(MockResponse().setBody("{\"data\":\"0x3529b5834ea3c6\"}"))
        ccipReadFetcher.fetchCcipRead(
            transactionCall = TransactionCall(
                to = Network.ETH_MAINNET.ensAddress!!,
                data = "0x02".hexString
            ),
            calldata = "0x02".hexString,
            urls = listOf(mockWebServer.url("/").toString() + "{sender}/{data}")
        )

        mockWebServer.takeRequest().requestUrl.toString() shouldBeEqualTo "${mockWebServer.url("/")}${Network.ETH_MAINNET.ensAddress!!.value}/0x02"
    }

    @Test
    fun `should send post request if there is no data in the url`() = runTest {
        mockWebServer.enqueue(MockResponse().setBody("{\"data\":\"0x3529b5834ea3c6\"}"))
        ccipReadFetcher.fetchCcipRead(
            transactionCall = TransactionCall(
                to = Network.ETH_MAINNET.ensAddress!!,
                data = "0x02".hexString
            ),
            calldata = "0x02".hexString,
            urls = listOf(mockWebServer.url("/").toString() + "{sender}")
        )

        val request = mockWebServer.takeRequest()
        request.method shouldBeEqualTo "POST"
        request.body.readUtf8() shouldBeEqualTo "{\"data\":\"0x02\",\"sender\":\"0x00000000000c2e074ec69a0dfb2997ba6c7d2e1e\"}"
    }

    @Test
    fun `should send get request if there is data in the url`() = runTest {
        mockWebServer.enqueue(MockResponse().setBody("{\"data\":\"0x3529b5834ea3c6\"}"))
        ccipReadFetcher.fetchCcipRead(
            transactionCall = TransactionCall(
                to = Network.ETH_MAINNET.ensAddress!!,
                data = "0x02".hexString
            ),
            calldata = "0x02".hexString,
            urls = listOf(mockWebServer.url("/").toString() + "{sender}/{data}")
        )

        mockWebServer.takeRequest().method shouldBeEqualTo "GET"
    }

    @Test
    fun `should return null if no data from read url`() = runTest {
        mockWebServer.enqueue(MockResponse().setBody("{\"data\": null}"))
        val result = ccipReadFetcher.fetchCcipRead(
            transactionCall = TransactionCall(
                to = Network.ETH_MAINNET.ensAddress!!,
                data = "0x02".hexString
            ),
            calldata = "0x02".hexString,
            urls = listOf(
                mockWebServer.url("/").toString() + "{sender}/{data})"
            )
        )

        result shouldBeEqualTo null
    }

    @Test
    fun `should return try other urls if error code is out of 499 range`() = runTest {
        mockWebServer.enqueue(MockResponse().setResponseCode(509))
        mockWebServer.enqueue(MockResponse().setBody("{\"data\":\"0x3529b5834ea3c6\"}"))
        val result = ccipReadFetcher.fetchCcipRead(
            transactionCall = TransactionCall(
                to = Network.ETH_MAINNET.ensAddress!!,
                data = "0x02".hexString
            ),
            calldata = "0x02".hexString,
            urls = listOf(
                mockWebServer.url("/").toString() + "{sender}/{data}",
                mockWebServer.url("/").toString() + "{sender}/{data}",
            )
        )

        result shouldBeEqualTo "0x3529b5834ea3c6".hexString
    }

    @Test
    fun `should return null if code is between 400 and 499`() = runTest {
        mockWebServer.enqueue(MockResponse().setResponseCode(400))
        val result = ccipReadFetcher.fetchCcipRead(
            transactionCall = TransactionCall(
                to = Network.ETH_MAINNET.ensAddress!!,
                data = "0x02".hexString
            ),
            calldata = "0x02".hexString,
            urls = listOf(mockWebServer.url("/").toString() + "{sender}/{data}")
        )

        result shouldBeEqualTo null
    }
}