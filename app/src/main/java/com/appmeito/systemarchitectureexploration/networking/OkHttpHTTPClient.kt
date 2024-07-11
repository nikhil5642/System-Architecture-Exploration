package com.appmeito.systemarchitectureexploration.networking

import com.appmeito.systemarchitectureexploration.networking.HttpHelper.BASE_URL
import okhttp3.ConnectionPool
import okhttp3.EventListener
import okhttp3.Headers
import okhttp3.OkHttpClient
import okhttp3.Protocol
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.logging.HttpLoggingInterceptor
import java.io.IOException
import java.util.concurrent.TimeUnit

class OkHttpHTTPClient(protocols: List<Protocol>) : NetworkInterface {
    private val client: OkHttpClient = OkHttpClient.Builder()
        .eventListener(ConnectionCountEventListener)
        .protocols(listOf(Protocol.HTTP_1_1))  // Enforce HTTP/1.1
        .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
        .build()

    private lateinit var endPoint: String
    private lateinit var response: String

    override suspend fun connect(endPoint: String) {
        this.endPoint = endPoint
    }

    override suspend fun getRequest() {
        val url = "$BASE_URL$endPoint"
        val request = Request.Builder()
            .url(url)
            .header("Connection", "keep-alive")
            .build()

        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) throw IOException("Unexpected code $response")
            this.response = response.body?.string() ?: "No response body"
        }
    }

    override suspend fun postRequest(data: ByteArray) {
        val url = "$BASE_URL$endPoint"
        val requestBody = data.toRequestBody("application/json; charset=utf-8".toMediaType())
        val request = Request.Builder()
            .url(url)
            .post(requestBody)
            .build()

        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) throw IOException("Unexpected code $response")
            this.response = response.body?.string() ?: "No response body"
        }
    }

    override suspend fun receive(): String {
        return response
    }

    override suspend fun close() {
        // OkHttp manages connections automatically, no action needed on close.
    }
}

object ConnectionCountEventListener : EventListener() {
    private var openConnectionCount = 0

    @Synchronized
    override fun connectionAcquired(call: okhttp3.Call, connection: okhttp3.Connection) {
        openConnectionCount++
        println("Connection acquired: Total open connections: $openConnectionCount")
    }

    @Synchronized
    override fun connectionReleased(call: okhttp3.Call, connection: okhttp3.Connection) {
        openConnectionCount--
        println("Connection released: Total open connections: $openConnectionCount")
    }

    fun getOpenConnectionCount(): Int {
        return openConnectionCount
    }
}