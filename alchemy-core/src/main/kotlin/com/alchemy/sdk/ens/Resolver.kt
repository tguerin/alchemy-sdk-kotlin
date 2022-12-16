package com.alchemy.sdk.ens

import com.alchemy.sdk.core.Core
import com.alchemy.sdk.core.model.Address
import com.alchemy.sdk.core.model.TransactionCall
import com.alchemy.sdk.util.Constants.HASH_ZERO
import com.alchemy.sdk.util.Formatters
import com.alchemy.sdk.util.HexString.Companion.hexString
import com.alchemy.sdk.util.encodeBytes

class Resolver(
    private val core: Core,
    private val resolverAddress: Address,
    private val ensName: Address.EnsAddress,
    private val dnsEncoder: DnsEncoder
) {

    private var _supportsEip2544: Boolean? = null

    suspend fun supportsWildcard(): Boolean {
        if (_supportsEip2544 == null) {
            val result = core.call(
                TransactionCall(
                    to = resolverAddress,
                    data = "0x01ffc9a79061b92300000000000000000000000000000000000000000000000000000000".hexString
                )
            )
            if (result.isFailure) {
                throw result.exceptionOrNull()!!
            } else {
                _supportsEip2544 = result.getOrThrow().intValue() == 1
            }
        }
        return _supportsEip2544 == true
    }

    suspend fun getAddress(): Address {
        var transactionCall = TransactionCall(
            to = resolverAddress,
            ccipReadEnabled = true,
            data = "0x3b3b57de".hexString + ensName.value
        )

        var parseBytes = false
        if (supportsWildcard()) {
            parseBytes = true

            transactionCall = transactionCall.copy(
                data = "0x9061b923".hexString + encodeBytes(
                    dnsEncoder.encode(ensName.rawAddress),
                    transactionCall.data!!
                )
            )
        }

        val result = core.call(transactionCall)

        var resultData = result.getOrThrow()

        if (parseBytes) {
            resultData = resultData.parseBytes(0)!!
        }

        if (resultData.data == "0x" || resultData.data == HASH_ZERO) throw IllegalStateException("Found 0x address")

        val formatCallAddress =
            Formatters.formatCallAddress(resultData) ?: throw IllegalStateException(
                "Couldn't parse $result as an Address"
            )
        return Address.from(formatCallAddress)
    }
}