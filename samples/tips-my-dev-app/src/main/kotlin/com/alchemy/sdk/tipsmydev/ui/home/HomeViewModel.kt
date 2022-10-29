package com.alchemy.sdk.tipsmydev.ui.home

import android.app.Application
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.alchemy.sdk.Alchemy
import com.alchemy.sdk.core.model.Address
import com.alchemy.sdk.util.Ether.Companion.ether
import com.alchemy.sdk.wallet.Wallet
import com.alchemy.sdk.wallet.WalletEvent
import com.alchemy.sdk.ws.model.WebsocketMethod
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val alchemy: Alchemy,
    private val wallet: Wallet,
    application: Application
) : AndroidViewModel(application) {

    private val errorMessageState = MutableStateFlow<String?>(null)
    private val waitingTransactionState = MutableStateFlow(false)

    private val walletState = wallet.events()
        .shareIn(
            scope = viewModelScope,
            replay = 1,
            started = SharingStarted.WhileSubscribed(5000),
        )

    private val addressState = walletState.map {
        if (it is WalletEvent.Connected) {
            it.address.value.data
        } else {
            ""
        }
    }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = ""
        )

    private val balanceState = addressState
        .filter { it.isNotEmpty() }
        .map {
            val balanceResult = alchemy.core.getBalance(Address.from(it))
            if (balanceResult.isSuccess) {
                balanceResult.getOrThrow().ether.toDouble()
            } else {
                errorMessageState.emit("Failed to retrieve balance")
                -1.0
            }
        }

    val state: StateFlow<HomeState> = combine(
        addressState,
        balanceState,
        waitingTransactionState,
        errorMessageState,
        ::HomeState
    )
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = HomeState()
        )

    fun connectWallet(context: Context) {
        viewModelScope.launch {
            wallet.connect { connectionUrl ->
                context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(connectionUrl)))
            }
        }
    }

    fun disconnectWallet() {
        viewModelScope.launch {
            wallet.disconnect()
        }
    }

    fun sendTransaction(context: Context, rawAmount: String) {
        viewModelScope.launch {
            val ether = rawAmount.toDouble().ether
            val transactionResult = wallet.sendTransaction(
                Address.from(addressState.value),
                Address.from("0xdE2182072cAc5dC3C0230363f1123C157A059e90"),
                ether
            ) { connectionUrl ->
                context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(connectionUrl)))
            }
            if (transactionResult.isSuccess) {
                waitingTransactionState.emit(true)
                val transactionReceiptResult =
                    alchemy.ws.on(WebsocketMethod.Transaction(transactionResult.getOrThrow()))
                        .first()
                if (transactionReceiptResult.isSuccess) {
                    waitingTransactionState.emit(false)
                }
            }
        }
    }
}