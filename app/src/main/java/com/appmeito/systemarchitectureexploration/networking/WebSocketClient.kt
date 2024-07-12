package com.appmeito.systemarchitectureexploration.networking

import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import okio.ByteString

class WebSocketClient(val client: OkHttpClient,val url: String) {
    private lateinit var webSocket: WebSocket

    private val listener = object : WebSocketListener() {
        override fun onOpen(webSocket: WebSocket, response: okhttp3.Response) {
            println("WebSocket: Opened")
        }

        override fun onMessage(webSocket: WebSocket, text: String) {
            println("Received: $text")
        }

        override fun onMessage(webSocket: WebSocket, bytes: ByteString) {
            println("Received bytes: $bytes")
        }

        override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
            webSocket.close(1000, null)
            println("Closing : $code / $reason")
        }

        override fun onFailure(webSocket: WebSocket, t: Throwable, response: okhttp3.Response?) {
            println("Error : " + t.message)
        }
    }

    fun connect(){
        val request = Request.Builder().url(url).build()
        webSocket = client.newWebSocket(request, listener)
    }
    fun disconnect(){
        webSocket.close(1000, "Goodbye, World!")
    }

    fun send(message: String) {
        webSocket.send(message)
    }
}
