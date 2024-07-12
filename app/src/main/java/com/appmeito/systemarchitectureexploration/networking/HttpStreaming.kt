package com.appmeito.systemarchitectureexploration.networking

import android.content.Context
import okhttp3.*
import java.io.File
import java.io.IOException

class HttpStreaming{

    fun setupStreaming(client: OkHttpClient,url: String) {
        //This is also server sent events (SSE)
        val request = Request.Builder()
            .url(url)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
            }

            override fun onResponse(call: Call, response: Response) {
                response.body?.let { responseBody ->
                    val source = responseBody.source()
                    try {
                        while (!source.exhausted()) {
                            val line = source.readUtf8Line()
                            println("Received stream data: $line")
                        }
                    } catch (e: IOException) {
                        e.printStackTrace()
                    } finally {
                        responseBody.close()
                    }
                }
            }
        })
    }

    fun downloadFile(context: Context, client: OkHttpClient, url: String, outputFilePath: String) {
        val request = Request.Builder()
            .url(url)
            .build()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
            }

            override fun onResponse(call: Call, response: Response) {
                response.body?.byteStream()?.use { inputStream ->
                    File(context.getExternalFilesDir(null),outputFilePath).outputStream().use { fileOutputStream ->
                        inputStream.copyTo(fileOutputStream)
                    }
                }
            }
        })
    }

}
