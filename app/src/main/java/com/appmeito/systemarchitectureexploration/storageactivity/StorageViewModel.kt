package com.appmeito.systemarchitectureexploration.storageactivity

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.appmeito.systemarchitectureexploration.MainRepository
import com.appmeito.systemarchitectureexploration.StorageRepository
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

class StorageViewModel(val storageRepository: StorageRepository):ViewModel() {


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