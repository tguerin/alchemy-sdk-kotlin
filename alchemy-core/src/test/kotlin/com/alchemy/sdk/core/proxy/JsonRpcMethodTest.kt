package com.alchemy.sdk.core.proxy

import com.alchemy.sdk.core.model.Address
import com.alchemy.sdk.core.util.HexString
import com.alchemy.sdk.core.util.Wei
import com.alchemy.sdk.json.rpc.client.JsonRpcClient
import com.alchemy.sdk.json.rpc.client.annotation.JsonRpc
import com.alchemy.sdk.json.rpc.client.generator.IdGenerator
import com.alchemy.sdk.json.rpc.client.model.JsonRpcRequest
import io.mockk.coEvery
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit4.MockKRule
import kotlinx.coroutines.test.runTest
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldBeInstanceOf
import org.junit.Rule
import org.junit.Test
import java.io.IOException
import java.lang.reflect.Method
import java.lang.reflect.Parameter
import java.lang.reflect.ParameterizedType
import java.lang.reflect.WildcardType

@Suppress("UNCHECKED_CAST")
class JsonRpcMethodTest {

    @get:Rule
    val mockkRule = MockKRule(this)

    @MockK
    lateinit var idGenerator: IdGenerator

    @MockK
    lateinit var jsonRpcClient: JsonRpcClient

    @MockK
    lateinit var method: Method

    @MockK
    lateinit var parameter: Parameter

    @MockK
    lateinit var wildcardType: WildcardType

    @MockK
    lateinit var firstParameterizedType: ParameterizedType

    @MockK
    lateinit var secondParameterizedType: ParameterizedType

    @MockK
    lateinit var addressParameterConverter: ParameterConverter<Address, String>

    @Test
    fun `should parse annotation then delegate call to rpc client and return result on success`() =
        runTest {
            prepareCallEnvironment()

            coEvery {
                jsonRpcClient.call(
                    JsonRpcRequest(
                        "1",
                        "2.0",
                        "eth_getBalance",
                        arrayListOf("0x1188aa75c38e1790be3768508743fbe7b50b2153")
                    ),
                    Wei::class.java
                )
            } returns Result.success(Wei(HexString.from("0x3529b5834ea3c6")))

            val result = JsonRpcMethod.parseAnnotations<Wei>(
                idGenerator,
                jsonRpcClient,
                mapOf(
                    Address::class.java to addressParameterConverter as ParameterConverter<Any, Any>
                ),
                method
            ).invoke(arrayOf(Address.from("0x1188aa75C38E1790bE3768508743FBE7b50b2153")))

            result.isSuccess shouldBeEqualTo true
            result.getOrThrow() shouldBeEqualTo Wei(HexString.from("0x3529b5834ea3c6"))
        }

    @Test
    fun `should return result failure in case of failing call`() = runTest {
        prepareCallEnvironment()

        coEvery {
            jsonRpcClient.call(
                JsonRpcRequest(
                    "1",
                    "2.0",
                    "eth_getBalance",
                    arrayListOf("0x1188aa75c38e1790be3768508743fbe7b50b2153")
                ),
                Wei::class.java
            )
        } throws IOException("issue happened")

        val result = JsonRpcMethod.parseAnnotations<Wei>(
            idGenerator,
            jsonRpcClient,
            mapOf(
                Address::class.java to addressParameterConverter as ParameterConverter<Any, Any>
            ),
            method
        ).invoke(arrayOf(Address.from("0x1188aa75C38E1790bE3768508743FBE7b50b2153")))

        result.isSuccess shouldBeEqualTo false
        val exception = result.exceptionOrNull()
        exception shouldBeInstanceOf IOException::class.java
        exception?.message shouldBeEqualTo  "issue happened"
    }

    private fun prepareCallEnvironment() {
        every { idGenerator.generateId() } returns "1"
        every { method.annotations } returns arrayOf(JsonRpc("eth_getBalance"))
        coEvery { addressParameterConverter.convert(any()) } returns "0x1188aa75c38e1790be3768508743fbe7b50b2153"
        every { method.parameters } returns arrayOf(parameter)
        every { parameter.parameterizedType } returns firstParameterizedType
        every { firstParameterizedType.actualTypeArguments } returns arrayOf(wildcardType)
        every { wildcardType.lowerBounds } returns arrayOf(secondParameterizedType)
        every { secondParameterizedType.actualTypeArguments } returns arrayOf(Wei::class.java)
    }

}