package com.appmeito.systemarchitectureexploration.storageactivity

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.appmeito.systemarchitectureexploration.MainRepository
import com.appmeito.systemarchitectureexploration.StorageRepository

class StorageViewModelFactory(private val storageRepository: StorageRepository):ViewModelProvider.Factory{

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(StorageViewModel::class.java)) {
            StorageViewModel(storageRepository) as T
        } else {
            throw IllegalArgumentException("ViewModel Not Found")
        }
    }

}