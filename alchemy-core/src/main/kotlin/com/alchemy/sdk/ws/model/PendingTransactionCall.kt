package com.alchemy.sdk.ws.model

import kotlinx.serialization.Serializable

@Serializable
internal class PendingTransactionCall(
    val hashesOnly: Boolean = true
)
