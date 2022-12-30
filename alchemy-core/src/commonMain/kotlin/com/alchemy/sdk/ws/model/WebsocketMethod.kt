package com.alchemy.sdk.ws.model

import com.alchemy.sdk.core.api.CoreApi
import com.alchemy.sdk.core.model.Address
import com.alchemy.sdk.core.model.Log
import com.alchemy.sdk.core.model.TransactionReceipt
import com.alchemy.sdk.util.HexString
import com.alchemy.sdk.util.pmap
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.serialization.KSerializer
import kotlinx.serialization.builtins.serializer

sealed class WebsocketMethod<R>(
    val params: List<Pair<Any, KSerializer<out Any>>>,
    val serializer: KSerializer<R>,
    val name: String = "eth_subscribe",
) {

    abstract suspend fun resolveAddresses(core: CoreApi): WebsocketMethod<R>

    object Block : WebsocketMethod<BlockHead>(
        listOf("newHeads" to String.serializer()),
        BlockHead.serializer()
    ) {
        override suspend fun resolveAddresses(core: CoreApi): WebsocketMethod<BlockHead> {
            return this
        }
    }

    data class LogFilter(
        val address: Address? = null,
        val topics: List<HexString> = emptyList(),
    ) : WebsocketMethod<Log>(
        mutableListOf("logs" to String.serializer(), LogFilterCall(address, topics) to LogFilterCall.serializer()),
        Log.serializer()
    ) {
        override suspend fun resolveAddresses(core: CoreApi): WebsocketMethod<Log> {
            return if (address == null) {
                this
            } else {
                LogFilter(
                    core.resolveAddress(address).getOrThrow(),
                    topics
                )
            }
        }
    }

    data class PendingTransactions(
        val fromAddress: Address? = null,
        val toAddresses: List<Address> = emptyList(),
        val hashesOnly: Boolean = false,
    ) : WebsocketMethod<PendingTransaction>(
        mutableListOf(
            "alchemy_pendingTransactions" to String.serializer(),
            PendingTransactionCall(hashesOnly) to PendingTransactionCall.serializer()
        ),
        PendingTransaction.serializer()
    ) {
        override suspend fun resolveAddresses(core: CoreApi): WebsocketMethod<PendingTransaction> = coroutineScope {
            val fromAddressDeferred = async {
                if (fromAddress == null) null else core.resolveAddress(fromAddress).getOrThrow()
            }
            val toAddressesResolved = toAddresses.pmap { core.resolveAddress(it).getOrThrow() }
            PendingTransactions(
                fromAddressDeferred.await(),
                toAddressesResolved,
                hashesOnly
            )
        }
    }

    data class Transaction(
        val hash: HexString
    ) : WebsocketMethod<TransactionReceipt>(
        mutableListOf("newHeads" to String.serializer()),
        TransactionReceipt.serializer()
    ) {
        override suspend fun resolveAddresses(core: CoreApi): WebsocketMethod<TransactionReceipt> {
            return this
        }
    }

    data class UnSubscribe(
        private val subscriptionId: HexString
    ) : WebsocketMethod<Boolean>(
        listOf(subscriptionId to HexString.serializer()),
        Boolean.serializer(),
        "eth_unsubscribe"
    ) {
        override suspend fun resolveAddresses(core: CoreApi): WebsocketMethod<Boolean> {
            return this
        }
    }
}