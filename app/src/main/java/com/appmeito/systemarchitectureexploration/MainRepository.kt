package com.appmeito.systemarchitectureexploration

import com.appmeito.systemarchitectureexploration.networking.NetworkInterface

class MainRepository(private val networkInterface: NetworkInterface) {

    suspend fun getText():String{
        networkInterface.connect("/sendText")
        networkInterface.getRequest()
        val response = networkInterface.receive()
        networkInterface.close()
        return response
    }
}