package com.appmeito.systemarchitectureexploration.networking


interface NetworkInterface {
    suspend fun connect(endPoint: String)
    suspend fun getRequest()
    suspend fun postRequest(data: ByteArray)
    suspend fun receive(): String
    suspend fun close()
}
