package com.alchemy.sdk.transact

import com.alchemy.sdk.core.Core
import com.alchemy.sdk.core.model.CancelPrivateTransactionRequest
import com.alchemy.sdk.core.model.PrivateTransactionCall
import com.alchemy.sdk.util.HexString

class Transact internal constructor(
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