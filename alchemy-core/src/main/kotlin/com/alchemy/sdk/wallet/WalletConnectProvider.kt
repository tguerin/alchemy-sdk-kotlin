package com.alchemy.sdk.wallet

import com.alchemy.sdk.core.model.Address
import com.alchemy.sdk.util.Ether
import com.alchemy.sdk.util.HexString
import kotlinx.coroutines.flow.Flow

interface WalletConnectProvider {

    suspend fun connect(connectAction: (String) -> Unit)

    suspend fun disconnect()

    fun events(): Flow<WalletEvent>

    suspend fun sendTransaction(
        from: Address,
        to: Address,
        nonce: HexString,
        gasPrice: Ether,
        gasLimit: HexString?,
        value: Ether,
        data: HexString?,
        connectionCallback: (String) -> Unit
    ): Result<HexString>
}

sealed interface WalletEvent {
    data class Connected(val address: Address) : WalletEvent
    data class Disconnected(val reason: DisconnectionReason = DisconnectionReason.Normal) :
        WalletEvent {
        enum class DisconnectionReason {
            Normal, NoAvailableAccount
        }
    }

    data class Error(val exception: Throwable?) : WalletEvent
}