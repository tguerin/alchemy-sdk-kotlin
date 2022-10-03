package com.alchemy.sdk.core

import com.alchemy.sdk.core.model.core.Address
import com.alchemy.sdk.core.model.core.TransactionCall
import com.alchemy.sdk.core.util.Constants.HASH_ZERO
import com.alchemy.sdk.core.util.Formatters
import com.alchemy.sdk.core.util.HexString.Companion.hexString

internal class Resolver(
    private val core: Core,
    private val resolverAddress: Address.ContractAddress,
    private val ensName: Address.EnsAddress
) {
    fun supportsWildcard(): Boolean {
        // TODO add support check
        return true
    }

    suspend fun getAddress(): Address {
        val result = core.call(
            TransactionCall(
                to = resolverAddress,
                ccipReadEnabled = true,
                data = "0x3b3b57de".hexString + ensName.value
            )
        ).getOrThrow()

        if (result.data == "0x" || result.data == HASH_ZERO) throw IllegalStateException("Found 0x address")

        val formatCallAddress = Formatters.formatCallAddress(result) ?: throw IllegalStateException(
            "Couldn't parse $result as an Address"
        )
        return Address.from(formatCallAddress)
    }
}