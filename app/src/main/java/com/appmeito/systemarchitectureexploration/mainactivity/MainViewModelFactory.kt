package com.appmeito.systemarchitectureexploration.mainactivity

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.appmeito.systemarchitectureexploration.MainRepository

class MainViewModelFactory(val mainRepository: MainRepository):ViewModelProvider.Factory{

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            MainViewModel(mainRepository) as T
        } else {
            throw IllegalArgumentException("ViewModel Not Found")
        }
    }

}