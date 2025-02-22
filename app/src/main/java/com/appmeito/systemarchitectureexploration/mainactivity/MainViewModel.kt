package com.appmeito.systemarchitectureexploration.mainactivity

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.apollographql.apollo3.ApolloClient
import com.appmeito.systemarchitectureexploration.MainRepository
import com.appmeito.systemarchitectureexploration.networking.GraphQLClient
import com.appmeito.systemarchitectureexploration.networking.GrpcClient
import com.appmeito.systemarchitectureexploration.networking.HttpHelper.BASE_URL
import com.appmeito.systemarchitectureexploration.networking.HttpHelper.BASE_HOST
import com.appmeito.systemarchitectureexploration.networking.HttpStreaming
import com.appmeito.systemarchitectureexploration.networking.PollingService
import com.appmeito.systemarchitectureexploration.networking.WebSocketClient
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Protocol
import java.util.concurrent.TimeUnit

class MainViewModel(val repository: MainRepository):ViewModel() {

    val client = OkHttpClient.Builder()
        .protocols(listOf(Protocol.HTTP_2,Protocol.HTTP_1_1))
        .readTimeout(60, TimeUnit.SECONDS)  // Increase read timeout
        .connectTimeout(60, TimeUnit.SECONDS)  // Increase connection timeout
        .build()
    private val pollingService = PollingService(client, "$BASE_URL/polling/poll")

    private val webSocketClient= WebSocketClient(client,"$BASE_URL/ws")

    private val graphQLClient=GraphQLClient(client,"$BASE_URL/graphql")


    fun testGraphQL(){
        viewModelScope.launch (Dispatchers.IO){
            val response=graphQLClient.getUser(1)
            println("GraphQL response: $response")
        }
    }

    fun testGrpcClient(){
        viewModelScope.launch(Dispatchers.IO) {
            val client = GrpcClient.create(BASE_HOST, 50051)
            try{
                val response = client.sayHello("World")
                println("Response: $response")
            }catch (e: Exception){
                e.printStackTrace()
            }

            // Server Streaming example
            client.streamGreetings("World")

            // Client Streaming example
            client.sendGreetings(listOf("Alice", "Bob", "Charlie"))

            // Bidirectional Streaming example
            client.chat()

            delay(100000)
            client.shutDown()
        }
    }

    fun testGrpcFileDownload(){
        val client = GrpcClient.create(BASE_HOST, 50051)
        client.downloadFile("DappLinker Polygon Grant Submission.pdf.zip") {
            client.shutDown()
        }
    }

    fun webSocketConnect(){
        webSocketClient.connect()
    }
    fun testWebSocketSend(){
        webSocketClient.send("Websocket working on Mobile")
    }
    fun webSocketDisConnect(){
        webSocketClient.disconnect()
    }


    fun startHTTPStreaming(){
        viewModelScope.launch(Dispatchers.IO) {
            HttpStreaming().setupStreaming(client,"$BASE_URL/stream")
        }

    }

    fun startLongPolling(){
        pollingService.startLongPolling { data ->
            // Update UI with the received data
            println("Data received: $data")
        }
    }
    fun startShortPolling(){
        viewModelScope.launch(Dispatchers.IO) {
            pollingService.startShortPolling { data ->
                // Update UI with the received data
                println("Data received: $data")
            }
            delay(10000L)
            pollingService.stopShortPolling()
        }
    }
    fun onButtonPress(){
        viewModelScope.launch (Dispatchers.IO){
            for(i in 0..10){
                try {
                    val text = repository.getText()
                    Log.d("MainViewModel","Received text: $text $i")
                } catch (e: Exception) {
                    Log.e("MainViewModel", "Error fetching text", e)
                }
                delay(1000)
            }
        }
    }

    fun generateFCMCToken(){
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w("FCM", "Fetching FCM registration token failed", task.exception)
                return@addOnCompleteListener
            }

            // Get the FCM token
            val token = task.result
            Log.d("FCM", "FCM registration token: $token")

            // Save the token or send it to your server
        }
    }
}