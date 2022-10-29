package com.alchemy.sdk.tipsmydev.ui.home

data class HomeState(
    val address: String = "",
    val balance: Double = 0.0,
    val waitingTransaction: Boolean = false,
    val errorMessage: String? = null,
)