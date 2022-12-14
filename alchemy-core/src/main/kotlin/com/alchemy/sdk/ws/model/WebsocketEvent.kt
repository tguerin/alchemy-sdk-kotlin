package com.alchemy.sdk.ws.model

import com.alchemy.sdk.util.HexString

internal sealed interface WebsocketEvent {
    data class Data<T>(val subscriptionId: HexString, val data: T) : WebsocketEvent
    data class RawMessage(val message: String) : WebsocketEvent
    data class Status(val status: WebsocketStatus) : WebsocketEvent
    data class Subscription(val methodId: String, val subscriptionId: HexString) : WebsocketEvent
}