package com.appmeito.systemarchitectureexploration.mainactivity

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
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

import com.appmeito.systemarchitectureexploration.ui.theme.SystemArchitectureExplorationTheme

class MainActivity : ComponentActivity() {
    lateinit var mainViewModel: MainViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val mainRepository: MainRepository = MainRepository(HttpClientSelector.getClient(HTTPTYPES.HTTP11_OKHTTP))
        mainViewModel= ViewModelProvider(this, MainViewModelFactory(mainRepository))[MainViewModel::class.java]
        enableEdgeToEdge()
        setContent {
            SystemArchitectureExplorationTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    CustomButton(
                        title = "HTTP1.1 Request Test",
                        onButtonClick = {mainViewModel.onButtonPress()},
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun CustomButton(title: String,onButtonClick: () -> Unit, modifier: Modifier = Modifier) {
    Column(modifier = modifier.padding(16.dp)) {
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