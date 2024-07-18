package com.appmeito.systemarchitectureexploration.storageactivity

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.appmeito.systemarchitectureexploration.storage.StorageRepository
import com.appmeito.systemarchitectureexploration.mainactivity.CustomButton
import com.appmeito.systemarchitectureexploration.storage.room.AppDatabase
import com.appmeito.systemarchitectureexploration.storage.room.UserRepository

import com.appmeito.systemarchitectureexploration.ui.theme.SystemArchitectureExplorationTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class StorageActivity : ComponentActivity() {
    private lateinit var storageViewModel: StorageViewModel



    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        checkAndRequestPermissions()

        lifecycleScope.launch (Dispatchers.IO){
            val db = withContext(Dispatchers.IO) {
                AppDatabase.getDatabase(application)
            }
            val userRepository = UserRepository(db.userDao())

            storageViewModel = ViewModelProvider(this@StorageActivity, StorageViewModelFactory(StorageRepository(), userRepository))[StorageViewModel::class.java]
        }

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
                            title = "Test Shared Preferences",
                            onButtonClick = { storageViewModel.testSharedPreferences(this@StorageActivity) },
                        )
                        CustomButton(
                            title = "Test Internal Storage Token",
                            onButtonClick = { storageViewModel.testTokenInternalStorage(this@StorageActivity) },
                        )
                        CustomButton(
                            title = "Test External Storage Token",
                            onButtonClick = { storageViewModel.testTokenExternalStorage(this@StorageActivity) },
                        )
                        CustomButton(
                            title = "Test Datastore Storage Token",
                            onButtonClick = { storageViewModel.testSimpleDatastore(this@StorageActivity) },
                        )
                        CustomButton(
                            title = "Test Proto Datastore Storage Token",
                            onButtonClick = { storageViewModel.testProtoDatastore(this@StorageActivity) },
                        )
                        CustomButton(
                            title = "Add user, ROOM DB",
                            onButtonClick = { storageViewModel.addUser("nikhil","agrawal") },
                        )
                        CustomButton(
                            title = "Delete all users, ROOM DB",
                            onButtonClick = { storageViewModel.deleteAllUsers() },
                        )
                        CustomButton(
                            title = "Print all users, ROOM DB",
                            onButtonClick = { storageViewModel.listAvailableUsers() },
                        )
                    }
                }
            }
        }
    }
    fun checkAndRequestPermissions(){
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
    }
}


