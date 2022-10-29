package com.alchemy.sdk.wallet

import com.alchemy.sdk.core.Core
import com.alchemy.sdk.core.model.Address
import com.alchemy.sdk.util.Ether
import com.alchemy.sdk.util.HexString
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class Wallet(
    private val core: Core,
    private val walletConnectProvider: WalletConnectProvider
) {

    suspend fun connect(action: (String) -> Unit) {
        walletConnectProvider.connect(action)
    }

    suspend fun sendTransaction(
        from: Address,
        to: Address,
        value: Ether,
        connectionCallback: (String) -> Unit
    ): Result<HexString> = withContext(Dispatchers.IO) {
        val nonceAsync = async {
            core.getTransactionCount(from)
        }
        val gasPriceAsync = async {
            core.getGasPrice()
        }

        awaitAll(nonceAsync, gasPriceAsync)
        val nonceResult = nonceAsync.getCompleted()
        if (nonceResult.isFailure) {
            return@withContext Result.failure(
                nonceResult.exceptionOrNull() ?: RuntimeException("Failed to retrieve nonce")
            )
        }
        val gasPriceResult = gasPriceAsync.getCompleted()
        if (gasPriceResult.isFailure) {
            return@withContext Result.failure(
                gasPriceResult.exceptionOrNull() ?: RuntimeException("Failed to retrieve gas price")
            )
        }
        walletConnectProvider.sendTransaction(
            from,
            to,
            nonceResult.getOrThrow(),
            gasPriceResult.getOrThrow(),
            null,
            value,
            null,
            connectionCallback
        )
    }

    fun events(): Flow<WalletEvent> {
        return walletConnectProvider.events()
    }

    suspend fun disconnect() {
        walletConnectProvider.disconnect()
    }
}