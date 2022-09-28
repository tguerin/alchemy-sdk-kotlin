package com.alchemy.sdk.core

import com.alchemy.sdk.core.model.core.CancelPrivateTransactionRequest
import com.alchemy.sdk.core.model.core.PrivateTransactionCall
import com.alchemy.sdk.core.util.HexString

class Transact(
    private val core: Core
) {

    suspend fun getTransaction(transactionHash: HexString) =
        core.getTransactionByHash(transactionHash)

    suspend fun sendTransaction(signedTransaction: HexString) =
        core.sendRawTransaction(signedTransaction)

    suspend fun sendPrivateTransaction(privateTransactionCall: PrivateTransactionCall) =
        core.sendPrivateTransaction(privateTransactionCall)

    suspend fun sendPrivateTransaction(cancelPrivateTransactionRequest: CancelPrivateTransactionRequest) =
        core.cancelPrivateTransaction(cancelPrivateTransactionRequest)

}