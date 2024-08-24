package com.appmeito.systemarchitectureexploration.mainactivity2

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.appmeito.systemarchitectureexploration.MainRepository

class FlowViewModelFactory(val mainRepository: MainRepository):ViewModelProvider.Factory{

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(FlowViewModel::class.java)) {
            FlowViewModel(mainRepository) as T
        } else {
            throw IllegalArgumentException("ViewModel Not Found")
        }
    }

}