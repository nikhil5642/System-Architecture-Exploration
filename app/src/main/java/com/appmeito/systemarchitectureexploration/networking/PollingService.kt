package com.appmeito.systemarchitectureexploration.networking

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.*
import java.io.IOException

class PollingService(private val client: OkHttpClient, private val url: String) {
    private var shortPollingJob: Job?=null

    fun startLongPolling(action: (String) -> Unit) {
        println("start polling")
        val request = Request.Builder()
            .url(url)
            .build()

        callServer(request, action)
    }

    fun startShortPolling(interval: Long = 1000L, action: (String) -> Unit) {
        println("Start Short Polling")
        shortPollingJob= CoroutineScope(Dispatchers.IO).launch {
            while (isActive) {
                try {
                    val data = "abc"
                    withContext(Dispatchers.Main) {
                        action(data)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                delay(interval)
            }
        }
    }

    fun stopShortPolling(){
        println("Stop Short Polling")
        shortPollingJob?.cancel()
    }

    private fun callServer(request: Request, action: (String) -> Unit) {
        println("call Server")
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()  // Handle failure
            }

            override fun onResponse(call: Call, response: Response) {
                response.use {
                    if (!response.isSuccessful) throw IOException("Unexpected code $response")

                    val responseData = response.body?.string()
                    if (responseData != null) {
                        action(responseData)
                    }

                    // Immediately start another request for long polling
                    callServer(request, action)
                }
            }
        })
    }
}
