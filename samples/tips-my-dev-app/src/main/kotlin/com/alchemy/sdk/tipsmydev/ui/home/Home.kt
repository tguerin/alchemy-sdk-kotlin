package com.alchemy.sdk.tipsmydev.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.ExperimentalLifecycleComposeApi
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.alchemy.sdk.tipsmydev.R

@Composable
fun Home() {
    Home(viewModel())
}

@Composable
@OptIn(ExperimentalLifecycleComposeApi::class)
internal fun Home(
    viewModel: HomeViewModel
) {
    val context = LocalContext.current
    val state by viewModel.state.collectAsStateWithLifecycle()
    Home(
        state = state,
        onWalletConnectClicked = {
            viewModel.connectWallet(context)
        },
        disconnectWalletClicked = {
            viewModel.disconnectWallet()
        },
        sendTransactionClicked = {
            viewModel.sendTransaction(context, it)
        }
    )
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
internal fun Home(
    state: HomeState,
    onWalletConnectClicked: () -> Unit = {},
    disconnectWalletClicked: () -> Unit = {},
    sendTransactionClicked: (String) -> Unit = {},
) {

    var amount by remember { mutableStateOf(TextFieldValue("0.0")) }
    val snackbarHostState = remember { SnackbarHostState() }
    state.errorMessage?.let { errorMessage ->
        LaunchedEffect(errorMessage) {
            snackbarHostState.showSnackbar(errorMessage)
        }
    }
    
    if (state.waitingTransaction) {
        AlertDialog(
            onDismissRequest = { },
            title = {
                Text(text = "Transaction")
            },
            text = {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text("Waiting transaction to complete" )
                }
            },
            confirmButton = {}
        )
    }

    Scaffold(
        snackbarHost = {
            SnackbarHost(
                hostState = snackbarHostState,
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .fillMaxWidth()
            )
        },
        modifier = Modifier.fillMaxSize()
    ) {
        Surface(
            color = MaterialTheme.colorScheme.background,
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 16.dp, end = 16.dp, top = 16.dp)
        ) {
            if (state.address.isEmpty()) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Button(onClick = onWalletConnectClicked) {
                        Text(text = LocalContext.current.getString(R.string.connect_wallet))
                    }
                }
            } else {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = LocalContext.current.getString(
                            R.string.account,
                            state.address
                        )
                    )
                    Text(
                        text = LocalContext.current.getString(
                            R.string.balance,
                            state.balance.toString()
                        )
                    )
                    Button(
                        onClick = disconnectWalletClicked,
                        modifier = Modifier
                            .align(Alignment.End)
                            .padding(vertical = 8.dp)
                    ) {
                        Text(text = LocalContext.current.getString(R.string.disconnect_wallet))
                    }

                    OutlinedTextField(
                        value = amount,
                        label = { Text("Amount") },
                        onValueChange = { value ->
                            amount = value
                        },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Button(
                        onClick = { sendTransactionClicked(amount.text) },
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .padding(vertical = 8.dp)
                    ) {
                        Text(text = LocalContext.current.getString(R.string.send_tip))
                    }
                }
            }
        }
    }
}