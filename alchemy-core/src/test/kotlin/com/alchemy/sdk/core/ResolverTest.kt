package com.alchemy.sdk.core

import com.alchemy.sdk.core.model.core.Address
import com.alchemy.sdk.core.model.core.Network
import com.alchemy.sdk.core.model.core.TransactionCall
import com.alchemy.sdk.core.util.HexString.Companion.hexString
import io.mockk.coEvery
import io.mockk.impl.annotations.MockK
import io.mockk.junit4.MockKRule
import kotlinx.coroutines.test.runTest
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldBeInstanceOf
import org.junit.Rule
import org.junit.Test

class ResolverTest {

    @get:Rule
    val mockkRule = MockKRule(this)

    @MockK
    lateinit var core: Core

    @Test
    fun `should resolve ens address`() = runTest {
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
                    ccipReadEnabled = true,
                    data = "0x3b3b57de".hexString + ensName.value
                )
            )
        } returns Result.success("0x000000000000000000000000d8da6bf26964af9d7eed9e03e53415d37aa96045".hexString)

        val resolvedAddress = resolver.getAddress()
        resolvedAddress shouldBeEqualTo Address.from("0xd8da6bf26964af9d7eed9e03e53415d37aa96045")
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
                    ccipReadEnabled = true,
                    data = "0x3b3b57de".hexString + ensName.value
                )
            )
        } returns Result.failure(IllegalStateException("plop"))
        resolver.getAddress()
    }
}