package com.appmeito.systemarchitectureexploration.mainactivity

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import com.appmeito.systemarchitectureexploration.MainRepository
import com.appmeito.systemarchitectureexploration.networking.HTTPTYPES
import com.appmeito.systemarchitectureexploration.networking.HttpClientSelector
import com.appmeito.systemarchitectureexploration.networking.HttpHelper.BASE_URL
import com.appmeito.systemarchitectureexploration.networking.HttpStreaming

import com.appmeito.systemarchitectureexploration.ui.theme.SystemArchitectureExplorationTheme
import com.google.firebase.messaging.FirebaseMessaging

class MainActivity : ComponentActivity() {
    private lateinit var mainViewModel: MainViewModel
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 101)
            }
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), 100)
            }
            if (checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(arrayOf(Manifest.permission.POST_NOTIFICATIONS), 100)
            }
        }
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
        val mainRepository: MainRepository = MainRepository(HttpClientSelector.getClient(HTTPTYPES.HTTP11_OKHTTP))
        mainViewModel= ViewModelProvider(this, MainViewModelFactory(mainRepository))[MainViewModel::class.java]
        enableEdgeToEdge()
        setContent {
            SystemArchitectureExplorationTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Column(
                        modifier = Modifier
                            .padding(innerPadding)
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState()) // Makes the column scrollable
                    ) {
                        CustomButton(
                            title = "HTTP1.1 Request Test",
                            onButtonClick = {mainViewModel.onButtonPress()},
                        )
                        CustomButton(
                            title = "HTTP Long Polling Test",
                            onButtonClick = {mainViewModel.startLongPolling()},
                        )

                        CustomButton(
                            title = "HTTP Short Polling Test",
                            onButtonClick = {mainViewModel.startShortPolling()},
                        )
                        CustomButton(
                            title = "HTTP Streaming Test",
                            onButtonClick = {mainViewModel.startHTTPStreaming()},
                        )
                        CustomButton(
                            title = "HTTP Streaming File Download Test",
                            onButtonClick = {
                                HttpStreaming().downloadFile(
                                    context = this@MainActivity ,
                                    mainViewModel.client ,
                                    "$BASE_URL/download-file",
                                    "large_file.zip")},
                        )
                        CustomButton(
                            title = "Websocket Connect",
                            onButtonClick = {
                              mainViewModel.webSocketConnect() },
                        )
                        CustomButton(
                            title = "Test Websocket send",
                            onButtonClick = {
                                mainViewModel.testWebSocketSend() },
                        )
                        CustomButton(
                            title = "WebSocket Disconnect",
                            onButtonClick = {
                                mainViewModel.webSocketDisConnect() },
                        )

                        CustomButton(
                            title = "Test GRPC Client",
                            onButtonClick = {
                                mainViewModel.testGrpcClient() },
                        )

                        CustomButton(
                            title = "Test GRPC File Download",
                            onButtonClick = {
                                mainViewModel.testGrpcFileDownload() },
                        )

                        CustomButton(
                            title = "Test GraphQL",
                            onButtonClick = {
                                mainViewModel.testGraphQL() },
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun CustomButton(title: String,onButtonClick: () -> Unit, modifier: Modifier = Modifier) {
    Column(modifier = modifier.padding(start = 16.dp,bottom = 8.dp)) {
        Button(onClick = onButtonClick) {
            Text(title)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    SystemArchitectureExplorationTheme {
        CustomButton("Press Me",{println("Button Pressed")})
    }
}