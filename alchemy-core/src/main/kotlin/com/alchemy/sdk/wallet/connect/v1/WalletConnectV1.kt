package com.alchemy.sdk.wallet.connect.v1

import com.alchemy.sdk.core.model.Address
import com.alchemy.sdk.util.Ether
import com.alchemy.sdk.util.GsonUtil
import com.alchemy.sdk.util.HexString
import com.alchemy.sdk.util.HexString.Companion.hexString
import com.alchemy.sdk.wallet.WalletConnectProvider
import com.alchemy.sdk.wallet.WalletEvent
import com.alchemy.sdk.wallet.WalletEvent.Disconnected.DisconnectionReason
import com.alchemy.sdk.wallet.connect.v1.BridgeServer.ConnectionState
import com.alchemy.sdk.wallet.connect.v1.WalletConnectV1.Action.Connect
import com.alchemy.sdk.wallet.connect.v1.WalletConnectV1.Action.Disconnect
import com.alchemy.sdk.wallet.connect.v1.impls.FileWCSessionStore
import com.alchemy.sdk.wallet.connect.v1.impls.WCSession
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import java.util.Random
import java.util.UUID
import java.util.concurrent.atomic.AtomicInteger

class WalletConnectV1(
    private val storageFile: FileWCSessionStore,
    private val payloadAdapter: Session.PayloadAdapter,
    private val encryptionAdapter: Session.PayloadEncryption,
    private val peerMeta: Session.PeerMeta,
    private val transportBuilder: Session.Transport.Builder
) : WalletConnectProvider {

    private val id = AtomicInteger(1)

    private val bridgeServer = BridgeServer(GsonUtil.gson)

    private val _actionQueue = MutableSharedFlow<Action>()

    private val _walletEvents = MutableStateFlow<WalletEvent>(WalletEvent.Disconnected())

    private val coroutineScope = CoroutineScope(Dispatchers.IO + CoroutineName("Wallet Connect"))

    private var currentSession: Session? = null
    private var currentSessionConfig: Session.Config? = null

    private val actionQueue by lazy {
        _actionQueue
            .shareIn(
                scope = coroutineScope,
                replay = 1,
                started = SharingStarted.Eagerly
            )
            .combine(bridgeServer.state) { action, serverState ->
                if (action is Connect && serverState !is ConnectionState.Connected) {
                    bridgeServer.start()
                } else if (action is Disconnect && serverState !is ConnectionState.Disconnected) {
                    currentSession?.clearCallbacks()
                    currentSession?.kill()
                    currentSession = null
                    bridgeServer.stop()
                }
                action to serverState
            }
            .map { (action, serverState) ->
                if (action is Connect && serverState is ConnectionState.Connected) {
                    val key = ByteArray(32).also { keyArray ->
                        Random().nextBytes(keyArray)
                    }.hexString.withoutPrefix()
                    val sessionConfig = Session.Config(
                        UUID.randomUUID().toString(),
                        "http://localhost:${serverState.port}",
                        key
                    )
                    val session = WCSession(
                        sessionConfig.toFullyQualifiedConfig(),
                        payloadAdapter,
                        encryptionAdapter,
                        storageFile,
                        null,
                        transportBuilder,
                        peerMeta
                    )
                    session.addCallback(
                        object : Session.Callback {
                            override fun onMethodCall(call: Session.MethodCall) {
                                if (call is Session.MethodCall.SendTransaction) {

                                }
                            }

                            override fun onStatus(status: Session.Status) {
                                coroutineScope.launch {
                                    when (status) {
                                        Session.Status.Updated,
                                        Session.Status.Approved -> {
                                            val approvedAccounts = session.approvedAccounts()
                                            if (approvedAccounts?.isNotEmpty() == true) {
                                                _walletEvents.emit(
                                                    WalletEvent.Connected(
                                                        Address.from(
                                                            approvedAccounts[0]
                                                        )
                                                    )
                                                )
                                            } else {
                                                _walletEvents.emit(
                                                    WalletEvent.Disconnected(
                                                        DisconnectionReason.NoAvailableAccount
                                                    )
                                                )
                                            }
                                        }
                                        Session.Status.Closed,
                                        Session.Status.Disconnected -> {
                                            _walletEvents.emit(
                                                WalletEvent.Disconnected()
                                            )
                                        }
                                        is Session.Status.Error -> {
                                            _walletEvents.emit(
                                                WalletEvent.Error(status.throwable)
                                            )
                                        }
                                        else -> {
                                            // just ignore
                                        }
                                    }
                                }
                            }
                        }
                    )
                    session.offer()
                    action.connectAction(sessionConfig.toWCUri())
                    currentSession = session
                    currentSessionConfig = sessionConfig
                }
            }
            .launchIn(coroutineScope)
    }

    override suspend fun connect(connectAction: (String) -> Unit) = withContext(Dispatchers.IO) {
        actionQueue // init the action queue
        disconnect()
        _actionQueue.emit(Connect(connectAction))
    }

    override suspend fun disconnect() {
        _actionQueue.emit(Disconnect)
    }

    override fun events(): Flow<WalletEvent> {
        return _walletEvents
    }

    override suspend fun sendTransaction(
        from: Address,
        to: Address,
        nonce: HexString,
        gasPrice: Ether,
        gasLimit: HexString?,
        value: Ether,
        data: HexString?,
        connectionCallback: (String) -> Unit
    ): Result<HexString> {
        return suspendCancellableCoroutine { continuation ->
            currentSession?.performMethodCall(
                Session.MethodCall.SendTransaction(
                    id = id.getAndIncrement().toLong(),
                    from = from.value.data,
                    to = to.value.data,
                    nonce = nonce.data,
                    gasLimit = gasLimit?.data,
                    gasPrice = gasPrice.weiHexValue.data,
                    value = value.weiHexValue.data,
                    data = data?.data ?: ""
                )
            ) { response ->
                if (response.result != null) {
                    continuation.resumeWith(Result.success(Result.success((response.result as String).hexString)))
                } else if (response.error != null) {
                    continuation.resumeWith(Result.failure(RuntimeException(response.error.message)))
                } else {
                    continuation.resumeWith(Result.failure(RuntimeException("Unknown error")))
                }

            } ?: continuation.resumeWith(Result.failure(RuntimeException("No session")))
            currentSessionConfig?.let {
                connectionCallback(it.toWCUri())
            }
        }
    }

    sealed interface Action {
        class Connect(val connectAction: (String) -> Unit) : Action
        object Disconnect : Action
    }
}