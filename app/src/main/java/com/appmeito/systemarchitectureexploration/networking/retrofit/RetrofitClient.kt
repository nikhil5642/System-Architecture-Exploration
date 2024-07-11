package com.appmeito.systemarchitectureexploration.networking.retrofit

import com.appmeito.systemarchitectureexploration.networking.HttpHelper.BASE_URL
import com.appmeito.systemarchitectureexploration.networking.NetworkInterface
import com.google.gson.JsonObject
import okhttp3.OkHttpClient
import okhttp3.Protocol
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException

class RetrofitClient(protocols: List<Protocol>) : NetworkInterface {
    private val client= createClient(BASE_URL,protocols)


    private fun createClient(baseUrl: String, protocols:List<Protocol>): RetrofitApiService {

        val client = OkHttpClient.Builder()
            .protocols(protocols)
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        return retrofit.create(RetrofitApiService::class.java)
    }

    private lateinit var endPoint: String
    private lateinit var response: String

    override suspend fun connect(endPoint: String) {
        this.endPoint = endPoint
    }

    override suspend fun getRequest() {
        val url = "$BASE_URL$endPoint"
        val call = client.get(url)
        executeCall(call)
    }


    override suspend fun postRequest(data: ByteArray) {
        val url = "$BASE_URL$endPoint"
        val jsonData = String(data, Charsets.UTF_8)
        val call = client.post(url, jsonData)
        executeCall(call)
    }
    private fun executeCall(call: Call<JsonObject>) {
        try {
            val response = call.execute()
            if (response.isSuccessful) {
                this.response= response.body().toString() ?: "No response body"
            } else {
                throw IOException("Unexpected code $response")
            }
        } catch (e: IOException) {
            "Error fetching data: ${e.message}"
        }
    }

    override suspend fun receive(): String {
        return response
    }

    override suspend fun close() {
        // OkHttp manages connections automatically, no action needed on close.
    }
}
