package com.appmeito.systemarchitectureexploration.storageactivity

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.appmeito.systemarchitectureexploration.storage.StorageRepository
import com.appmeito.systemarchitectureexploration.storage.room.UserRepository

class StorageViewModelFactory(private val storageRepository: StorageRepository,private val userRepository: UserRepository):ViewModelProvider.Factory{

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(StorageViewModel::class.java)) {
            StorageViewModel(storageRepository,userRepository) as T
        } else {
            throw IllegalArgumentException("ViewModel Not Found")
        }
    }

}