package com.appmeito.systemarchitectureexploration.networking.http11

import com.appmeito.systemarchitectureexploration.networking.HttpHelper.BASE_URL
import com.appmeito.systemarchitectureexploration.networking.NetworkInterface
import java.net.HttpURLConnection
import java.net.URL

class HTTP11ClientHttpURLConnection : NetworkInterface {
    private var connection: HttpURLConnection? = null

    override suspend fun connect(endPoint: String) {
        val url = URL("$BASE_URL$endPoint")
        connection = url.openConnection() as HttpURLConnection
        connection?.setRequestProperty("Connection", "keep-alive")
    }


    override suspend fun getRequest() {
        connection?.requestMethod = "GET"
    }

    override suspend fun postRequest(data: ByteArray) {
        connection?.apply {
            requestMethod = "POST"
            doOutput = true
            setRequestProperty("Content-Type", "application/json")
            outputStream.use { it.write(data) }
        }
    }

    override suspend fun receive(): String {
        return connection?.inputStream?.bufferedReader().use { it?.readText() ?: "" } ?: ""
    }

    override suspend fun close() {
        connection?.disconnect()
    }
}
