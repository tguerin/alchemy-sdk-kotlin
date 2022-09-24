package com.alchemy.sdk.core.proxy

import com.alchemy.sdk.core.Core
import com.alchemy.sdk.core.model.core.Address
import com.alchemy.sdk.core.model.core.BlockTag
import com.alchemy.sdk.core.util.Ether
import com.alchemy.sdk.core.util.Ether.Companion.wei
import com.alchemy.sdk.json.rpc.client.annotation.JsonRpc
import com.alchemy.sdk.json.rpc.client.generator.IdGenerator
import com.alchemy.sdk.json.rpc.client.http.HttpJsonRpcClient
import com.alchemy.sdk.json.rpc.client.model.JsonRpcRequest
import io.mockk.coEvery
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit4.MockKRule
import kotlinx.coroutines.test.runTest
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldBeInstanceOf
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.io.IOException

class AlchemyProxyTest {

    interface CoreApi {
        @JsonRpc("eth_getBalance")
        suspend fun getBalance(
            address: Address,
            blockTag: BlockTag = BlockTag.Latest
        ): Result<Ether>
    }

    @get:Rule
    val mockkRule = MockKRule(this)

    @MockK
    lateinit var idGenerator: IdGenerator

    @MockK
    lateinit var jsonRpcClient: HttpJsonRpcClient

    @MockK
    lateinit var addressParameterConverter: ParameterConverter<Address, String>

    @MockK
    lateinit var blockTagParameterConverter: ParameterConverter<BlockTag, String>

    private lateinit var alchemyProxy: AlchemyProxy

    @Before
    @Suppress("UNCHECKED_CAST")
    fun setUp() {
        alchemyProxy = AlchemyProxy(
            idGenerator,
            jsonRpcClient
        )
    }

    @Test(expected = IllegalArgumentException::class)
    fun `should throw error if api is not interface`() {
        alchemyProxy.createProxy(Core::class.java)
    }

    @Test
    fun `should delegate call to rpc client`() = runTest {
        val alchemy = alchemyProxy.createProxy(CoreApi::class.java)
        val targetAddress = Address.from("0x1188aa75c38e1790be3768508743fbe7b50b2153")
        val expectedBalance = "0x3529b5834ea3c6".wei
        every { idGenerator.generateId() } returns "1"
        coEvery {
            addressParameterConverter.convert(targetAddress)
        } returns "0x1188aa75c38e1790be3768508743fbe7b50b2153"
        coEvery {
            blockTagParameterConverter.convert(BlockTag.Latest)
        } returns "latest"
        coEvery {
            jsonRpcClient.call<Ether>(
                JsonRpcRequest(
                    "1",
                    "2.0",
                    "eth_getBalance",
                    listOf(targetAddress, BlockTag.Latest)
                ),
                Ether::class.java
            )
        } returns Result.success(expectedBalance)

        val result = alchemy.getBalance(targetAddress)

        result.isSuccess shouldBeEqualTo true
        result.getOrThrow() shouldBeEqualTo expectedBalance
    }

    @Test
    fun `should return failure if rpc client fails`() = runTest {
        val alchemy = alchemyProxy.createProxy(CoreApi::class.java)
        val targetAddress = Address.from("0x1188aa75c38e1790be3768508743fbe7b50b2153")
        every { idGenerator.generateId() } returns "1"
        coEvery {
            addressParameterConverter.convert(targetAddress)
        } returns "0x1188aa75c38e1790be3768508743fbe7b50b2153"
        coEvery {
            blockTagParameterConverter.convert(BlockTag.Latest)
        } returns "latest"
        coEvery {
            jsonRpcClient.call<Ether>(
                JsonRpcRequest(
                    "1",
                    "2.0",
                    "eth_getBalance",
                    listOf(targetAddress, BlockTag.Latest)
                ),
                Ether::class.java
            )
        } returns Result.failure(IOException("an error occured"))

        val result = alchemy.getBalance(targetAddress)

        result.isSuccess shouldBeEqualTo false
        val exception = result.exceptionOrNull()
        exception shouldBeInstanceOf IOException::class.java
        exception?.message shouldBeEqualTo "an error occured"
    }


}