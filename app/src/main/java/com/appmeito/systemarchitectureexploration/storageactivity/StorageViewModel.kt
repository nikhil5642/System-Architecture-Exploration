package com.appmeito.systemarchitectureexploration.storageactivity

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.viewModelFactory
import com.appmeito.systemarchitectureexploration.adapter.GetUserQuery_ResponseAdapter
import com.appmeito.systemarchitectureexploration.storage.StorageRepository
import com.appmeito.systemarchitectureexploration.storage.room.User
import com.appmeito.systemarchitectureexploration.storage.room.UserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class StorageViewModel(private val storageRepository: StorageRepository,
                       private val userRepository: UserRepository):ViewModel() {


    fun addUser(name: String, email: String) {
        viewModelScope.launch (Dispatchers.IO){
            userRepository.insertUser(User(name = name, email = email))
        }
    }

    fun deleteAllUsers() {
        viewModelScope.launch (Dispatchers.IO){
            userRepository.deleteAllUsers()
        }
    }
    fun listAvailableUsers(){
        viewModelScope.launch (Dispatchers.IO){
            userRepository.getAllUsers().forEach {
                val name=it.name
                println("User List $name)")
            }
        }
    }

   fun testSharedPreferences(context: Context){
       storageRepository.saveTokenSharedPref(context,"Shared Prefrence Token")
       val token=storageRepository.getTokenSharedPref(context)
       println("Token found: $token")
   }

    fun testTokenInternalStorage(context: Context){
        storageRepository.saveTokenInternalStorage(context,"Shared Internal storage Token")
        val token=storageRepository.getTokenInternalStorage(context)
        println("Token found: $token")
    }

    fun testTokenExternalStorage(context: Context){
        storageRepository.saveTokenExternalStorage(context,"Shared External storage Token")
        val token=storageRepository.getTokenExternalStorage(context)
        println("Token found: $token")
    }

    fun testSimpleDatastore(context: Context){
        viewModelScope.launch (Dispatchers.IO){
            storageRepository.saveTokenSimpleDataStore(context,"Simple datastore storage Token")
            storageRepository.getTokenSimpleDataStore(context).collect{
                println("Token found: $it")
            }
        }
    }

    fun testProtoDatastore(context: Context){
        viewModelScope.launch (Dispatchers.IO){
            storageRepository.saveTokenProtoDataStore(context,"Proto datastore storage Token")
            storageRepository.getTokenProtoDataStore(context).collect{
                println("Token found: $it")
            }
        }
    }

}