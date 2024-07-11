package com.appmeito.systemarchitectureexploration.mainactivity

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.appmeito.systemarchitectureexploration.MainRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainViewModel(val repository: MainRepository):ViewModel() {

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
}