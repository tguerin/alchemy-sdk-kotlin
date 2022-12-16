package com.alchemy.sdk.core

import com.alchemy.sdk.ccip.CcipReadFetcher
import com.alchemy.sdk.core.api.CoreApi
import com.alchemy.sdk.core.model.Address
import com.alchemy.sdk.core.model.BlockTag
import com.alchemy.sdk.core.model.Index
import com.alchemy.sdk.core.model.Log
import com.alchemy.sdk.core.model.LogFilter
import com.alchemy.sdk.core.model.Network
import com.alchemy.sdk.core.model.Proof
import com.alchemy.sdk.core.model.TransactionCall
import com.alchemy.sdk.ens.DnsEncoder
import com.alchemy.sdk.ens.Resolver
import com.alchemy.sdk.rpc.model.JsonRpcException
import com.alchemy.sdk.util.Ether
import com.alchemy.sdk.util.Formatters
import com.alchemy.sdk.util.HexString
import com.alchemy.sdk.util.HexString.Companion.hexString
import com.alchemy.sdk.util.encodeBytes
import java.io.IOException
import java.util.concurrent.ConcurrentHashMap

@Suppress("ThrowableNotThrown")
class Core(
    private val network: Network,
    private val ccipReadFetcher: CcipReadFetcher,
    private val coreApi: CoreApi,
    private val dnsEncoder: DnsEncoder
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

    override suspend fun call(
        transactionCall: TransactionCall,
        blockTag: BlockTag,
    ): Result<HexString> {
        return call(transactionCall, blockTag, 0)
    }

    private suspend fun call(
        transactionCall: TransactionCall,
        blockTag: BlockTag,
        attempt: Int
    ): Result<HexString> {
        return resolveTransactionAddresses(transactionCall) {
            if (attempt >= MAX_CCIP_REDIRECTS) {
                Result.failure(IOException("error.too.many.ccip.redirects"))
            } else {
                val result = coreApi.call(this)
                if (result.isFailure) {
                    handleCallFailure(transactionCall, blockTag, result, attempt)
                } else {
                    result
                }
            }
        }
    }

    private suspend fun handleCallFailure(
        transactionCall: TransactionCall,
        blockTag: BlockTag,
        result: Result<HexString>,
        attempt: Int
    ): Result<HexString> {
        return when {
            result.exceptionOrNull() is JsonRpcException -> {
                val jsonRpcError = (result.exceptionOrNull() as JsonRpcException).jsonRpcError
                val errorData = jsonRpcError.data?.hexString ?: "0x".hexString
                if (
                    blockTag == BlockTag.Latest &&
                    errorData.data.substring(0, 10) == "0x556f1830" &&
                    errorData.length() % 32 == 4
                ) {
                    val newResult = try {
                        val data = errorData.slice(4)

                        // Check the sender of the OffchainLookup matches the transaction
                        /*val sender = data.slice(0, 32)
                        if (sender != transactionCall.to.value) {
                            // TODO log the issue
                        }*/

                        // Read the URLs from the response
                        val urls: MutableList<String?> = mutableListOf()
                        val urlsOffset = data.slice(32, 64).intValue()
                        val urlsLength = data.slice(urlsOffset, urlsOffset + 32).intValue()
                        val urlsData = data.slice(urlsOffset + 32)
                        for (u in 0 until urlsLength) {
                            val url = urlsData.parseString(u * 32)
                            urls.add(url)
                        }

                        // Get the CCIP calldata to forward
                        val calldata = data.parseBytes(64)

                        val callbackSelector = data.slice(96, 100)

                        // Get the extra data to send back to the contract as context
                        val extraData = data.parseBytes(128)

                        val ccipResult = ccipReadFetcher.fetchCcipRead(
                            transactionCall,
                            calldata,
                            urls.filterNotNull()
                        )

                        val params = listOfNotNull(ccipResult, extraData)
                        val newTransaction = TransactionCall(
                            to = transactionCall.to,
                            data = callbackSelector + encodeBytes(*params.toTypedArray())
                        )
                        call(newTransaction, blockTag, attempt + 1)
                    } catch (e: Exception) {
                        Result.failure(e)
                    }
                    newResult
                } else {
                    Result.failure(result.exceptionOrNull()!!)
                }
            }

            else -> {
                Result.failure(result.exceptionOrNull()!!)
            }
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

    internal suspend fun resolveAddress(address: Address): Result<Address> {
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

    internal suspend fun getResolver(ensAddress: Address.EnsAddress): Resolver {
        var currentEnsAddress = ensAddress
        while (true) {
            if (currentEnsAddress.rawAddress == "" || currentEnsAddress.rawAddress == ".") {
                error("Invalid address: ${ensAddress.rawAddress}")
            }

            // Optimization since the eth node cannot change and does
            // not have a wildcard resolver
            if (ensAddress.rawAddress != "eth" && currentEnsAddress.rawAddress == "eth") {
                error("Invalid address: ${ensAddress.rawAddress}")
            }

            // Check the current node for a resolver
            val resolverAddress = getResolverAddress(currentEnsAddress)

            // Found a resolver!
            if (resolverAddress != null) {
                val resolver = Resolver(this, resolverAddress, ensAddress, dnsEncoder)

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

    private suspend fun getResolverAddress(ensAddress: Address.EnsAddress): Address? {
        val network = getNetwork()
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
        val formattedAddress = Formatters.formatCallAddress(resolverAddress)
        return if (formattedAddress == null) null else Address.from(formattedAddress)
    }

    companion object {
        private const val MAX_CCIP_REDIRECTS = 10
    }

}