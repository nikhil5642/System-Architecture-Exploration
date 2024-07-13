package com.appmeito.systemarchitectureexploration.mainactivity

import android.Manifest
import android.app.job.JobInfo
import android.app.job.JobScheduler
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.IBinder
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
import com.appmeito.systemarchitectureexploration.services.MyBackgroundService
import com.appmeito.systemarchitectureexploration.services.MyBoundService
import com.appmeito.systemarchitectureexploration.services.MyForegroundService
import com.appmeito.systemarchitectureexploration.services.MyJobService

import com.appmeito.systemarchitectureexploration.ui.theme.SystemArchitectureExplorationTheme

class MainActivity : ComponentActivity() {
    private lateinit var mainViewModel: MainViewModel
    private var myService: MyBoundService? = null
    private var isBound = false

    private val connection = object : ServiceConnection {
        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            val binder = service as MyBoundService.LocalBinder
            myService = binder.getService()
            isBound = true
            myService?.performAction()
        }

        override fun onServiceDisconnected(arg0: ComponentName) {
            isBound = false
        }
    }

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
            if (checkSelfPermission(Manifest.permission.RECEIVE_BOOT_COMPLETED) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(arrayOf(Manifest.permission.RECEIVE_BOOT_COMPLETED), 100)
            }
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
                        CustomButton(
                            title = "Generate FCM token",
                            onButtonClick = {
                                mainViewModel.generateFCMCToken() },
                        )
                        CustomButton(
                            title = "Start Background Service",
                            onButtonClick = {
                                val serviceIntent = Intent(this@MainActivity, MyBackgroundService::class.java)
                                startService(serviceIntent)
                            },
                        )
                        CustomButton(
                            title = "Start ForeGround Service",
                            onButtonClick = {
                                val serviceIntent = Intent(this@MainActivity, MyForegroundService::class.java)
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                    startForegroundService(serviceIntent)
                                } else {
                                    startService(serviceIntent)
                                }
                            },
                        )
                        CustomButton(
                            title = "Start Bind Service",
                            onButtonClick = {
                                Intent(this@MainActivity, MyBoundService::class.java).also { intent ->
                                    bindService(intent, connection, Context.BIND_AUTO_CREATE)
                                } },
                        )
                        CustomButton(
                            title = "Schedule Job",
                            onButtonClick = {
                                val componentName = ComponentName(this@MainActivity, MyJobService::class.java)
                                val jobInfo = JobInfo.Builder(1, componentName)
                                    .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                                    .setPersisted(true)
                                    .setPeriodic(15 * 60 * 1000) // 15 minutes interval
                                    .build()

                                val jobScheduler = getSystemService(Context.JOB_SCHEDULER_SERVICE) as JobScheduler
                                jobScheduler.schedule(jobInfo) },

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