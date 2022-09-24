package com.alchemy.sdk.core.proxy

import com.alchemy.sdk.core.model.core.Address
import com.alchemy.sdk.core.util.Ether
import com.alchemy.sdk.core.util.Ether.Companion.wei
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

    @Test
    fun `should parse annotation then delegate call to rpc client and return result on success`() =
        runTest {
            prepareCallEnvironment()

            coEvery {
                jsonRpcClient.call<Ether>(
                    JsonRpcRequest(
                        "1",
                        "2.0",
                        "eth_getBalance",
                        arrayListOf(Address.from("0x1188aa75c38e1790be3768508743fbe7b50b2153"))
                    ),
                    Ether::class.java
                )
            } returns Result.success("0x3529b5834ea3c6".wei)

            val result = JsonRpcMethod.parseAnnotations<Ether>(
                idGenerator,
                jsonRpcClient,
                method
            ).invoke(arrayOf(Address.from("0x1188aa75C38E1790bE3768508743FBE7b50b2153")))

            result.isSuccess shouldBeEqualTo true
            result.getOrThrow() shouldBeEqualTo "0x3529b5834ea3c6".wei
        }

    @Test
    fun `should return result failure in case of failing call`() = runTest {
        prepareCallEnvironment()

        coEvery {
            jsonRpcClient.call<Ether>(
                JsonRpcRequest(
                    "1",
                    "2.0",
                    "eth_getBalance",
                    arrayListOf(Address.from("0x1188aa75c38e1790be3768508743fbe7b50b2153"))
                ),
                Ether::class.java
            )
        } throws IOException("issue happened")

        val result = JsonRpcMethod.parseAnnotations<Ether>(
            idGenerator,
            jsonRpcClient,
            method
        ).invoke(arrayOf(Address.from("0x1188aa75C38E1790bE3768508743FBE7b50b2153")))

        result.isSuccess shouldBeEqualTo false
        val exception = result.exceptionOrNull()
        exception shouldBeInstanceOf IOException::class.java
        exception?.message shouldBeEqualTo "issue happened"
    }

    private fun prepareCallEnvironment() {
        every { idGenerator.generateId() } returns "1"
        every { method.annotations } returns arrayOf(JsonRpc("eth_getBalance"))
        every { method.parameters } returns arrayOf(parameter)
        every { parameter.parameterizedType } returns firstParameterizedType
        every { firstParameterizedType.actualTypeArguments } returns arrayOf(wildcardType)
        every { wildcardType.lowerBounds } returns arrayOf(secondParameterizedType)
        every { secondParameterizedType.actualTypeArguments } returns arrayOf(Ether::class.java)
    }

}