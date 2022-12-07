package com.alchemy.sdk.ws

interface WebSocketListener {
    fun onClosed(code: Int, reason: String)

    fun onClosing(code: Int, reason: String)

    fun onFailure(t: Throwable)

    fun onMessage(text: String)

    fun onOpen()
}