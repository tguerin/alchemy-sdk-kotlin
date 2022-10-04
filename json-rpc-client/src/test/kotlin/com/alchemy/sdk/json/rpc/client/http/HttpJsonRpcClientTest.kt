package com.alchemy.sdk.json.rpc.client.http

import com.alchemy.sdk.json.rpc.client.model.JsonRpcError
import com.alchemy.sdk.json.rpc.client.model.JsonRpcException
import com.alchemy.sdk.json.rpc.client.model.JsonRpcRequest
import com.alchemy.sdk.json.rpc.client.model.JsonRpcResponse
import com.google.gson.Gson
import com.google.gson.TypeAdapter
import com.google.gson.reflect.TypeToken
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonToken
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldBeInstanceOf
import org.junit.After
import org.junit.Before
import org.junit.Test

class HttpJsonRpcClientTest {

    private val okHttpClient = OkHttpClient()

    private val gson = Gson()

    lateinit var httpJsonRpcClient: HttpJsonRpcClient

    private val mockWebServer = MockWebServer()

    @Before
    fun setUp() {
        mockWebServer.start()
        httpJsonRpcClient = HttpJsonRpcClient(mockWebServer.url("/").toString(), okHttpClient, gson)
    }

    @After
    fun tearDown() {
        mockWebServer.shutdown()
    }

    @Test
    fun `should deserialize result on success`() = runTest {
        mockWebServer.enqueue(MockResponse().setBody("{\"id\":\"1\",\"result\":\"0x3529b5834ea3c6\",\"error\":null}"))
        val jsonRpcRequest = JsonRpcRequest(
            "1",
            "2.0",
            "eth_getBalance",
            listOf("0x1188aa75c38e1790be3768508743fbe7b50b2153")
        )
        val result = httpJsonRpcClient.call<String>(jsonRpcRequest, String::class.java)

        val request = withContext(Dispatchers.IO) { mockWebServer.takeRequest() }
        request.body.peek()
            .readString(Charsets.UTF_8) shouldBeEqualTo "{\"id\":\"1\",\"jsonrpc\":\"2.0\",\"method\":\"eth_getBalance\",\"params\":[\"0x1188aa75c38e1790be3768508743fbe7b50b2153\"]}"
        result.isSuccess shouldBeEqualTo true
        result.getOrThrow() shouldBeEqualTo "0x3529b5834ea3c6"
    }

    @Test
    fun `should return failure on http fail`() = runTest {
        mockWebServer.enqueue(
            MockResponse().setResponseCode(400)
                .setBody("{\"id\":\"1\",\"result\":null,\"error\":{\"code\":1692,\"message\":\"an error occured\"}}")
        )
        val jsonRpcRequest = JsonRpcRequest(
            "1",
            "2.0",
            "eth_getBalance",
            listOf("0x1188aa75c38e1790be3768508743fbe7b50b2153")
        )
        val result = httpJsonRpcClient.call<String>(jsonRpcRequest, String::class.java)

        val request = withContext(Dispatchers.IO) { mockWebServer.takeRequest() }
        request.body.peek()
            .readString(Charsets.UTF_8) shouldBeEqualTo "{\"id\":\"1\",\"jsonrpc\":\"2.0\",\"method\":\"eth_getBalance\",\"params\":[\"0x1188aa75c38e1790be3768508743fbe7b50b2153\"]}"
        result.isFailure shouldBeEqualTo true
        val exception = result.exceptionOrNull()
        exception shouldBeInstanceOf RuntimeException::class.java
        exception?.message shouldBeEqualTo "error.http.code.400: an error occured"
    }

    @Test
    fun `should return failure on json rpc error`() = runTest {
        mockWebServer.enqueue(
            MockResponse().setResponseCode(200)
                .setBody("{\"id\":\"1\",\"result\":null,\"error\":{\"code\":1692,\"message\":\"an error occured\"}}")
        )
        val jsonRpcRequest = JsonRpcRequest(
            "1",
            "2.0",
            "eth_getBalance",
            listOf("0x1188aa75c38e1790be3768508743fbe7b50b2153")
        )
        val result = httpJsonRpcClient.call<String>(jsonRpcRequest, String::class.java)

        val request = withContext(Dispatchers.IO) { mockWebServer.takeRequest() }
        request.body.peek()
            .readString(Charsets.UTF_8) shouldBeEqualTo "{\"id\":\"1\",\"jsonrpc\":\"2.0\",\"method\":\"eth_getBalance\",\"params\":[\"0x1188aa75c38e1790be3768508743fbe7b50b2153\"]}"
        result.isFailure shouldBeEqualTo true
        val exception = result.exceptionOrNull()
        exception shouldBeInstanceOf JsonRpcException::class.java
        val jsonRpcException = exception as JsonRpcException
        jsonRpcException.jsonRpcError shouldBeEqualTo JsonRpcError(
            1692,
            "an error occured"
        )
    }


    @Test
    fun `should return failure on invalid json content`() = runTest {
        mockWebServer.enqueue(MockResponse().setBody("{\"id\":\"1\",\"0x3529b5834ea3c6\",\"error\":null}"))
        val jsonRpcRequest = JsonRpcRequest(
            "1",
            "2.0",
            "eth_getBalance",
            listOf("0x1188aa75c38e1790be3768508743fbe7b50b2153")
        )
        val result = httpJsonRpcClient.call<String>(jsonRpcRequest, String::class.java)

        val request = withContext(Dispatchers.IO) { mockWebServer.takeRequest() }
        request.body.peek()
            .readString(Charsets.UTF_8) shouldBeEqualTo "{\"id\":\"1\",\"jsonrpc\":\"2.0\",\"method\":\"eth_getBalance\",\"params\":[\"0x1188aa75c38e1790be3768508743fbe7b50b2153\"]}"
        result.isFailure shouldBeEqualTo true
        val exception = result.exceptionOrNull()
        exception shouldBeInstanceOf RuntimeException::class.java
        exception?.message shouldBeEqualTo "error.invalid.body"
    }

    @Test
    fun `should return failure on json content not fully consumed`() = runTest {
        val jsonRpcRequest = JsonRpcRequest(
            "1",
            "2.0",
            "eth_getBalance",
            listOf("0x1188aa75c38e1790be3768508743fbe7b50b2153")
        )
        val mockGson = mockk<Gson>()
        val mockAdapter = mockk<TypeAdapter<JsonRpcResponse<String>>>()
        val mockJsonReader = mockk<JsonReader>()
        every { mockGson.toJson(jsonRpcRequest) } returns "{\"id\":\"1\",\"jsonrpc\":\"2.0\",\"method\":\"eth_getBalance\",\"params\":[\"0x1188aa75c38e1790be3768508743fbe7b50b2153\"]}"
        every { mockGson.newJsonReader(any()) } returns mockJsonReader
        every {
            mockGson.getAdapter(
                TypeToken.getParameterized(
                    JsonRpcResponse::class.java,
                    String::class.java
                )
            )
        } returns mockAdapter
        every { mockAdapter.read(mockJsonReader) } returns JsonRpcResponse(
            "1",
            "0x3529b5834ea3c6",
            null
        )
        every { mockJsonReader.peek() } returns JsonToken.BEGIN_ARRAY

        httpJsonRpcClient =
            HttpJsonRpcClient(mockWebServer.url("/").toString(), okHttpClient, mockGson)
        mockWebServer.enqueue(MockResponse().setBody("{\"id\":\"1\",\"result\":\"0x3529b5834ea3c6\",\"error\":null}"))

        val result = httpJsonRpcClient.call<String>(jsonRpcRequest, String::class.java)

        val request = withContext(Dispatchers.IO) { mockWebServer.takeRequest() }
        request.body.peek()
            .readString(Charsets.UTF_8) shouldBeEqualTo "{\"id\":\"1\",\"jsonrpc\":\"2.0\",\"method\":\"eth_getBalance\",\"params\":[\"0x1188aa75c38e1790be3768508743fbe7b50b2153\"]}"
        result.isFailure shouldBeEqualTo true
        val exception = result.exceptionOrNull()
        exception shouldBeInstanceOf RuntimeException::class.java
        exception?.message shouldBeEqualTo "error.invalid.body"
    }

}