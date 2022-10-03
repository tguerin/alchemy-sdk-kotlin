package com.alchemy.sdk.core

import com.alchemy.sdk.core.api.CoreApi
import com.alchemy.sdk.core.model.core.Address
import com.alchemy.sdk.core.model.core.BlockTag
import com.alchemy.sdk.core.model.core.Index
import com.alchemy.sdk.core.model.core.Log
import com.alchemy.sdk.core.model.core.LogFilter
import com.alchemy.sdk.core.model.core.Network
import com.alchemy.sdk.core.model.core.Proof
import com.alchemy.sdk.core.model.core.TransactionCall
import com.alchemy.sdk.core.util.Ether
import com.alchemy.sdk.core.util.Formatters
import com.alchemy.sdk.core.util.HexString
import com.alchemy.sdk.core.util.HexString.Companion.hexString
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import java.util.concurrent.ConcurrentHashMap

class Core(
    private val network: Network,
    private val coreApi: CoreApi
) : CoreApi by coreApi {

    private val resolvedAddresses: MutableMap<Address.EnsAddress, Address> = ConcurrentHashMap()

    fun getAccounts(): Result<List<Address>> = Result.success(emptyList())

    fun getNetwork(): Network {
        return network
    }

    override suspend fun getBalance(address: Address, blockTag: BlockTag): Result<Ether> {
        return resolveAddress(address) {
            coreApi.getBalance(this, blockTag)
        }
    }

    override suspend fun getStorageAt(
        address: Address,
        index: Index,
        blockTag: BlockTag
    ): Result<HexString> {
        return resolveAddress(address) {
            coreApi.getStorageAt(address, index, blockTag)
        }
    }

    override suspend fun getProof(
        address: Address,
        keys: List<HexString>,
        blockTag: BlockTag
    ): Result<Proof> {
        return resolveAddress(address) {
            coreApi.getProof(address, keys, blockTag)
        }
    }

    override suspend fun getTransactionCount(
        address: Address,
        blockTag: BlockTag
    ): Result<HexString> {
        return resolveAddress(address) {
            coreApi.getTransactionCount(address, blockTag)
        }
    }

    override suspend fun call(transactionCall: TransactionCall): Result<HexString> {
        return resolveTransactionAddresses(transactionCall) {
            coreApi.call(this)
        }
    }


    override suspend fun estimateGas(transactionCall: TransactionCall): Result<HexString> {
        return resolveTransactionAddresses(transactionCall) {
            coreApi.estimateGas(this)
        }
    }

    override suspend fun getLogs(filter: LogFilter): Result<List<Log>> {
        return resolveLogFilterAddresses(filter) {
            coreApi.getLogs(this)
        }
    }

    override suspend fun newFilter(filter: LogFilter): Result<HexString> {
        return resolveLogFilterAddresses(filter) {
            coreApi.newFilter(this)
        }
    }

    internal suspend fun <T> resolveLogFilterAddresses(
        filter: LogFilter,
        block: suspend LogFilter.() -> Result<T>
    ): Result<T> {
        return when (filter) {
            is LogFilter.BlockHashFilter -> {
                block(filter.copy(addresses = resolveAddresses(filter.addresses)))
            }
            is LogFilter.BlockRangeFilter -> {
                block(filter.copy(addresses = resolveAddresses(filter.addresses)))
            }
        }
    }

    private suspend fun resolveAddresses(addresses: List<Address>): List<Address> {
        return addresses.map { resolveAddress(it).getOrThrow() }
    }

    internal suspend fun <T> resolveTransactionAddresses(
        transactionCall: TransactionCall,
        block: suspend (TransactionCall.() -> Result<T>)
    ): Result<T> {
        val resolvedFromAddressResult = if (transactionCall.from != null) {
            resolveAddress(transactionCall.from)
        } else {
            null
        }
        val resolvedToAddressResult = resolveAddress(transactionCall.to)
        return when {
            resolvedToAddressResult.isFailure -> {
                Result.failure(resolvedToAddressResult.exceptionOrNull()!!)
            }
            resolvedFromAddressResult?.isFailure == true -> {
                Result.failure(resolvedFromAddressResult.exceptionOrNull()!!)
            }
            else -> {
                block(
                    transactionCall.copy(
                        from = resolvedFromAddressResult?.getOrThrow(),
                        to = resolvedToAddressResult.getOrThrow()
                    )
                )
            }
        }
    }

    internal suspend fun <T> resolveAddress(
        address: Address,
        block: suspend (Address.() -> Result<T>)
    ): Result<T> {
        val resolvedAddressResult = resolveAddress(address)
        return if (resolvedAddressResult.isFailure) {
            Result.failure(resolvedAddressResult.exceptionOrNull()!!)
        } else {
            block(resolvedAddressResult.getOrThrow())
        }
    }

    private suspend fun resolveAddress(address: Address): Result<Address> {
        return try {
            val resolvedAddress = when (address) {
                is Address.EnsAddress -> {
                    resolvedAddresses.getOrPut(address) { getResolver(address).getAddress() }
                }
                else -> address
            }
            Result.success(resolvedAddress)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private suspend fun getResolver(ensAddress: Address.EnsAddress): Resolver {
        var currentEnsAddress = ensAddress;
        while (true) {
            if (currentEnsAddress.rawAddress == "" || currentEnsAddress.rawAddress == ".") {
                throw IllegalArgumentException("Invalid address: ${ensAddress.rawAddress}")
            }

            // Optimization since the eth node cannot change and does
            // not have a wildcard resolver
            if (ensAddress.rawAddress != "eth" && currentEnsAddress.rawAddress == "eth") {
                throw IllegalArgumentException("Invalid address: ${ensAddress.rawAddress}")
            }

            // Check the current node for a resolver
            val resolverAddress = getResolverAddress(currentEnsAddress);

            // Found a resolver!
            if (resolverAddress != null) {
                val resolver = Resolver(this, resolverAddress, ensAddress);

                // Legacy resolver found, using EIP-2544 so it isn't safe to use
                if (currentEnsAddress != ensAddress && !resolver.supportsWildcard()) {
                    throw IllegalStateException("Legacy resolver found, unsafe to use it")
                }

                return resolver
            }

            // Get the parent node
            val split = currentEnsAddress.rawAddress.split(".")
            currentEnsAddress = Address.from(
                split.slice(1 until split.size).joinToString(".")
            ) as Address.EnsAddress
        }

    }

    private suspend fun getResolverAddress(ensAddress: Address.EnsAddress): Address.ContractAddress? {
        val network = getNetwork();
        if (network.ensAddress == null) {
            throw IllegalArgumentException("${network.networkId} doesn't support ens resolution")
        }

        val resolverAddressResult = coreApi.call(
            TransactionCall(
                to = network.ensAddress,
                data = "0x0178b8bf".hexString + ensAddress.value
            )
        )
        val resolverAddress = resolverAddressResult.getOrThrow()
        val formattedAddress =
            Formatters.formatCallAddress(resolverAddress)
                ?: throw IllegalStateException("Can't format resolver address $resolverAddress")
        return Address.ContractAddress(formattedAddress.hexString)
    }

}