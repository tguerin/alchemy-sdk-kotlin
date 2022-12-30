package com.alchemy.sdk.ccip

import com.alchemy.sdk.ResourceUtils.Companion.json
import com.alchemy.sdk.core.model.Network
import com.alchemy.sdk.core.model.TransactionCall
import com.alchemy.sdk.shouldBeEqualTo
import com.alchemy.sdk.util.HexString.Companion.hexString
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.MockRequestHandleScope
import io.ktor.client.engine.mock.respond
import io.ktor.client.engine.mock.respondError
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.HttpRequestData
import io.ktor.client.request.HttpResponseData
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.http.content.TextContent
import io.ktor.http.headersOf
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class CcipReadFetcherTest {

    @Test
    fun `should get data from ccip read url`() = runTest {
        val ccipReadFetcher = setupCcipReadFetcher {
            respond(
                content = "{\"data\":\"0x3529b5834ea3c6\"}",
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, "application/json")
            )
        }
        val result = ccipReadFetcher.fetchCcipRead(
            transactionCall = TransactionCall(
                to = Network.ETH_MAINNET.ensAddress!!,
                data = "0x02".hexString
            ),
            callData = "0x02".hexString,
            urls = listOf("http://127.0.0.1/{sender}/{data}")
        )

        result shouldBeEqualTo "0x3529b5834ea3c6".hexString
    }

    @Test
    fun `validate request params`() = runTest {
        val ccipReadFetcher = setupCcipReadFetcher { request ->
            assertEquals(request.url.toString(), "http://127.0.0.1/${Network.ETH_MAINNET.ensAddress!!.value.data}/0x02")
            respond(
                content = "{\"data\":\"0x3529b5834ea3c6\"}",
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, "application/json")
            )
        }
        ccipReadFetcher.fetchCcipRead(
            transactionCall = TransactionCall(
                to = Network.ETH_MAINNET.ensAddress!!,
                data = "0x02".hexString
            ),
            callData = "0x02".hexString,
            urls = listOf("http://127.0.0.1/{sender}/{data}")
        )
    }

    @Test
    fun `should send post request if there is no data in the url`() = runTest {
        val ccipReadFetcher = setupCcipReadFetcher { request ->
            request.method shouldBeEqualTo HttpMethod.Post
            (request.body as TextContent).text shouldBeEqualTo "{\"data\":\"0x02\",\"sender\":\"0x00000000000c2e074ec69a0dfb2997ba6c7d2e1e\"}"
            respond(
                content = "{\"data\":\"0x3529b5834ea3c6\"}",
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, "application/json")
            )
        }
        ccipReadFetcher.fetchCcipRead(
            transactionCall = TransactionCall(
                to = Network.ETH_MAINNET.ensAddress!!,
                data = "0x02".hexString
            ),
            callData = "0x02".hexString,
            urls = listOf("http://127.0.0.1/{sender}")
        )

    }

    @Test
    fun `should send get request if there is data in the url`() = runTest {
        val ccipReadFetcher = setupCcipReadFetcher { request ->
            request.method shouldBeEqualTo HttpMethod.Get
            respond(
                content = "{\"data\":\"0x3529b5834ea3c6\"}",
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, "application/json")
            )
        }
        ccipReadFetcher.fetchCcipRead(
            transactionCall = TransactionCall(
                to = Network.ETH_MAINNET.ensAddress!!,
                data = "0x02".hexString
            ),
            callData = "0x02".hexString,
            urls = listOf("http://127.0.0.1/{sender}/{data}")
        )
    }

    @Test
    fun `should return null if no data from read url`() = runTest {
        val ccipReadFetcher = setupCcipReadFetcher {
            respond(
                content = "{\"data\": null}",
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, "application/json")
            )
        }
        val result = ccipReadFetcher.fetchCcipRead(
            transactionCall = TransactionCall(
                to = Network.ETH_MAINNET.ensAddress!!,
                data = "0x02".hexString
            ),
            callData = "0x02".hexString,
            urls = listOf("http://127.0.0.1/{sender}/{data}")
        )

        result shouldBeEqualTo null
    }

    @Test
    fun `should return try other urls if error code is out of 499 range`() = runTest {
        var sendError = true
        val ccipReadFetcher = setupCcipReadFetcher {
            if (sendError) {
                sendError = false
                respondError(HttpStatusCode.fromValue(509))
            } else {
                respond(
                    content = "{\"data\":\"0x3529b5834ea3c6\"}",
                    status = HttpStatusCode.OK,
                    headers = headersOf(HttpHeaders.ContentType, "application/json")
                )
            }
        }
        val result = ccipReadFetcher.fetchCcipRead(
            transactionCall = TransactionCall(
                to = Network.ETH_MAINNET.ensAddress!!,
                data = "0x02".hexString
            ),
            callData = "0x02".hexString,
            urls = listOf(
                "http://127.0.0.1/{sender}/{data}",
                "http://127.0.0.1/{sender}/{data}",
            )
        )

        result shouldBeEqualTo "0x3529b5834ea3c6".hexString
    }

    @Test
    fun `should return null if code is between 400 and 499`() = runTest {
        val ccipReadFetcher = setupCcipReadFetcher {
            respondError(HttpStatusCode.BadRequest)
        }
        val result = ccipReadFetcher.fetchCcipRead(
            transactionCall = TransactionCall(
                to = Network.ETH_MAINNET.ensAddress!!,
                data = "0x02".hexString
            ),
            callData = "0x02".hexString,
            urls = listOf("http://127.0.0.1/{sender}/{data}")
        )

        result shouldBeEqualTo null
    }

    private fun setupCcipReadFetcher(handleRequest: MockRequestHandleScope.(HttpRequestData) -> HttpResponseData): CcipReadFetcher {
        val ccipReadFetcher = CcipReadFetcher(
            HttpClient(
                MockEngine { request ->
                    handleRequest(request)
                }
            ) {
                install(ContentNegotiation) {
                    json(json)
                }
            }
        )
        return ccipReadFetcher
    }
}