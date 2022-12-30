package com.alchemy.sdk.ens

import com.alchemy.sdk.core.api.CoreApi
import com.alchemy.sdk.core.model.Address
import com.alchemy.sdk.core.model.BlockTag
import com.alchemy.sdk.core.model.Network
import com.alchemy.sdk.core.model.TransactionCall
import com.alchemy.sdk.shouldBeEqualTo
import com.alchemy.sdk.util.HexString.Companion.hexString
import com.alchemy.sdk.util.SdkResult
import io.mockative.Mock
import io.mockative.classOf
import io.mockative.given
import io.mockative.mock
import io.mockative.oneOf
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertFailsWith

class ResolverTest {

    private val dnsEncoder = DnsEncoder(EnsNormalizer)

    @Mock
    private val core = mock(classOf<CoreApi>())

    @Test
    fun `should resolve ens address without wildcard support`() = runTest {
        val ensName = Address.from("vitalik.eth") as Address.EnsAddress
        val resolver = Resolver(
            core,
            Network.ETH_MAINNET.ensAddress!!,
            ensName,
            dnsEncoder
        )

        given(core)
            .suspendFunction(core::call)
            .whenInvokedWith(
                oneOf(
                    TransactionCall(
                        to = Network.ETH_MAINNET.ensAddress!!,
                        data = "0x01ffc9a79061b92300000000000000000000000000000000000000000000000000000000".hexString
                    )
                ),
                oneOf(BlockTag.Latest)
            )
            .thenReturn(SdkResult.success("0x00".hexString))

        given(core)
            .suspendFunction(core::call)
            .whenInvokedWith(
                oneOf(
                    TransactionCall(
                        to = Network.ETH_MAINNET.ensAddress!!,
                        ccipReadEnabled = true,
                        data = "0x3b3b57de".hexString + ensName.value
                    )
                ),
                oneOf(BlockTag.Latest)
            )
            .thenReturn(SdkResult.success("0x000000000000000000000000d8da6bf26964af9d7eed9e03e53415d37aa96045".hexString))

        val resolvedAddress = resolver.getAddress()
        resolvedAddress shouldBeEqualTo Address.from("0xd8da6bf26964af9d7eed9e03e53415d37aa96045")
    }

    @Test
    fun `should resolve ens address with wildcard support`() = runTest {
        val ensName = Address.from("vitalik.eth") as Address.EnsAddress
        val resolver = Resolver(
            core,
            Network.ETH_MAINNET.ensAddress!!,
            ensName,
            dnsEncoder
        )

        given(core)
            .suspendFunction(core::call)
            .whenInvokedWith(
                oneOf(
                    TransactionCall(
                        to = Network.ETH_MAINNET.ensAddress!!,
                        data = "0x01ffc9a79061b92300000000000000000000000000000000000000000000000000000000".hexString
                    )
                ),
                oneOf(BlockTag.Latest)
            )
            .thenReturn(SdkResult.success("0x01".hexString))

        given(core)
            .suspendFunction(core::call)
            .whenInvokedWith(
                oneOf(
                    TransactionCall(
                        to = Network.ETH_MAINNET.ensAddress!!,
                        ccipReadEnabled = true,
                        data = "0x9061b92300000000000000000000000000000000000000000000000000000000000000400000000000000000000000000000000000000000000000000000000000000080000000000000000000000000000000000000000000000000000000000000000d07766974616c696b03657468000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000243b3b57deee6c4522aab0003e8d14cd40a6af439055fd2577951148c14b6cea9a5347583500000000000000000000000000000000000000000000000000000000".hexString
                    )
                ),
                oneOf(BlockTag.Latest)
            )
            .thenReturn(SdkResult.success("0x0000000000000000000000000000000000000000000000000000000000000020000000000000000000000000000000000000000000000000000000000000002000000000000000000000000041563129cdbbd0c5d3e1c86cf9563926b243834d".hexString))

        val resolvedAddress = resolver.getAddress()
        resolvedAddress shouldBeEqualTo Address.from("0x41563129cdbbd0c5d3e1c86cf9563926b243834d")
    }

    @Test
    fun `should throw exception if ens resolution failed`() = runTest {
        assertFailsWith<IllegalStateException> {
            val ensName = Address.from("vitalik.eth") as Address.EnsAddress
            val resolver = Resolver(
                core,
                Network.ETH_MAINNET.ensAddress!!,
                ensName,
                dnsEncoder
            )

            given(core)
                .suspendFunction(core::call)
                .whenInvokedWith(
                    oneOf(
                        TransactionCall(
                            to = Network.ETH_MAINNET.ensAddress!!,
                            data = "0x01ffc9a79061b92300000000000000000000000000000000000000000000000000000000".hexString
                        )
                    ),
                    oneOf(BlockTag.Latest)
                )
                .thenReturn(SdkResult.success("0x00".hexString))

            given(core)
                .suspendFunction(core::call)
                .whenInvokedWith(
                    oneOf(
                        TransactionCall(
                            to = Network.ETH_MAINNET.ensAddress!!,
                            ccipReadEnabled = true,
                            data = "0x3b3b57de".hexString + ensName.value
                        )
                    ),
                    oneOf(BlockTag.Latest)
                )
                .thenReturn(SdkResult.failure(IllegalStateException("plop")))
            resolver.getAddress()
        }

    }
}