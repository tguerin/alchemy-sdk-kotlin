package com.alchemy.sdk.core

import com.alchemy.sdk.core.ens.Resolver
import com.alchemy.sdk.core.model.core.Address
import com.alchemy.sdk.core.model.core.Network
import com.alchemy.sdk.core.model.core.TransactionCall
import com.alchemy.sdk.core.util.HexString.Companion.hexString
import io.mockk.coEvery
import io.mockk.impl.annotations.MockK
import io.mockk.junit4.MockKRule
import kotlinx.coroutines.test.runTest
import org.amshove.kluent.shouldBeEqualTo
import org.junit.Rule
import org.junit.Test

class ResolverTest {

    @get:Rule
    val mockkRule = MockKRule(this)

    @MockK
    lateinit var core: Core

    @Test
    fun `should resolve ens address without wildcard support`() = runTest {
        val ensName = Address.from("vitalik.eth") as Address.EnsAddress
        val resolver = Resolver(
            core,
            Network.ETH_MAINNET.ensAddress!!,
            ensName
        )

        coEvery {
            core.call(
                TransactionCall(
                    to = Network.ETH_MAINNET.ensAddress!!,
                    data = "0x01ffc9a79061b92300000000000000000000000000000000000000000000000000000000".hexString
                )
            )
        } returns Result.success("0x00".hexString)

        coEvery {
            core.call(
                TransactionCall(
                    to = Network.ETH_MAINNET.ensAddress!!,
                    ccipReadEnabled = true,
                    data = "0x3b3b57de".hexString + ensName.value
                )
            )
        } returns Result.success("0x000000000000000000000000d8da6bf26964af9d7eed9e03e53415d37aa96045".hexString)

        val resolvedAddress = resolver.getAddress()
        resolvedAddress shouldBeEqualTo Address.from("0xd8da6bf26964af9d7eed9e03e53415d37aa96045")
    }

    @Test
    fun `should resolve ens address with wildcard support`() = runTest {
        val ensName = Address.from("vitalik.eth") as Address.EnsAddress
        val resolver = Resolver(
            core,
            Network.ETH_MAINNET.ensAddress!!,
            ensName
        )

        coEvery {
            core.call(
                TransactionCall(
                    to = Network.ETH_MAINNET.ensAddress!!,
                    data = "0x01ffc9a79061b92300000000000000000000000000000000000000000000000000000000".hexString
                )
            )
        } returns Result.success("0x01".hexString)

        coEvery {
            core.call(
                TransactionCall(
                    to = Network.ETH_MAINNET.ensAddress!!,
                    ccipReadEnabled = true,
                    data = "0x9061b92300000000000000000000000000000000000000000000000000000000000000400000000000000000000000000000000000000000000000000000000000000080000000000000000000000000000000000000000000000000000000000000000d07766974616c696b03657468000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000243b3b57deee6c4522aab0003e8d14cd40a6af439055fd2577951148c14b6cea9a5347583500000000000000000000000000000000000000000000000000000000".hexString
                )
            )
        } returns Result.success("0x0000000000000000000000000000000000000000000000000000000000000020000000000000000000000000000000000000000000000000000000000000002000000000000000000000000041563129cdbbd0c5d3e1c86cf9563926b243834d".hexString)

        val resolvedAddress = resolver.getAddress()
        resolvedAddress shouldBeEqualTo Address.from("0x41563129cdbbd0c5d3e1c86cf9563926b243834d")
    }

    @Test(expected = IllegalStateException::class)
    fun `should throw exception if ens resolution failed`() = runTest {
        val ensName = Address.from("vitalik.eth") as Address.EnsAddress
        val resolver = Resolver(
            core,
            Network.ETH_MAINNET.ensAddress!!,
            ensName
        )

        coEvery {
            core.call(
                TransactionCall(
                    to = Network.ETH_MAINNET.ensAddress!!,
                    data = "0x01ffc9a79061b92300000000000000000000000000000000000000000000000000000000".hexString
                )
            )
        } returns Result.success("0x00".hexString)

        coEvery {
            core.call(
                TransactionCall(
                    to = Network.ETH_MAINNET.ensAddress!!,
                    ccipReadEnabled = true,
                    data = "0x3b3b57de".hexString + ensName.value
                )
            )
        } returns Result.failure(IllegalStateException("plop"))
        resolver.getAddress()
    }
}