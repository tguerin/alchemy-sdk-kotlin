package com.alchemy.sdk.ws.model

sealed interface WebsocketEvent<T> {
    class Data<T>(val data: T) : WebsocketEvent<T>
    class Status<T>(val status: WebsocketStatus) : WebsocketEvent<T>
}