package com.appmeito.systemarchitectureexploration.storage

import android.content.Context
import android.os.Environment
import androidx.datastore.core.CorruptionException
import androidx.datastore.core.DataStore
import androidx.datastore.core.Serializer
import androidx.datastore.dataStore
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.datastore.preferences.protobuf.InvalidProtocolBufferException
import com.appmeito.systemarchitectureexploration.storage.room.UserDao
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import protoDataStore.Protodatastore
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.InputStream
import java.io.OutputStream

class StorageRepository() {
    fun saveTokenSharedPref(context: Context, token: String) {
        val sharedPreferences = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("token", token)
        editor.apply()
    }

    // Retrieve a token
    fun getTokenSharedPref(context: Context): String? {
        val sharedPreferences = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        return sharedPreferences.getString("token", null)
    }

    fun saveTokenInternalStorage(context: Context, token: String) {
        val fileOutputStream: FileOutputStream = context.openFileOutput("token.txt", Context.MODE_PRIVATE)
        fileOutputStream.write(token.toByteArray())
        fileOutputStream.close()
    }

    // Retrieve a token
    fun getTokenInternalStorage(context: Context): String? {
        val fileInputStream: FileInputStream = context.openFileInput("token.txt")
        val token = fileInputStream.bufferedReader().use { it.readText() }
        fileInputStream.close()
        return token
    }

    fun saveTokenExternalStorage(context: Context, token: String) {
        if (Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED) {
            val file = File(context.getExternalFilesDir(null), "token.txt")
            val fileOutputStream = FileOutputStream(file)
            fileOutputStream.write(token.toByteArray())
            fileOutputStream.close()
        }
    }

    // Retrieve a token
    fun getTokenExternalStorage(context: Context): String? {
        val file = File(context.getExternalFilesDir(null), "token.txt")
        if (file.exists()) {
            val fileInputStream = FileInputStream(file)
            val token = fileInputStream.bufferedReader().use { it.readText() }
            fileInputStream.close()
            return token
        }
        return null
    }

    private val Context.dataStore by preferencesDataStore(name = "preferences_datastore")
    companion object {
        private val TOKEN_KEY = stringPreferencesKey("token")
    }

    suspend fun saveTokenSimpleDataStore(context: Context,token: String) {
        context.dataStore.edit { preferences ->
            preferences[TOKEN_KEY] = token
        }
    }

    fun getTokenSimpleDataStore(context: Context): Flow<String?> {
       return context.dataStore.data.map { preferences ->
            preferences[TOKEN_KEY]
        }
    }

    private val Context.protoDataStore: DataStore<Protodatastore.UserPreferences> by dataStore(
        fileName = "user_prefs.pb",
        serializer = UserPreferencesSerializer
    )

    suspend fun saveTokenProtoDataStore(context: Context,token: String) {
        context.protoDataStore.updateData { preferences ->
            preferences.toBuilder().setToken(token).build()
        }
    }

    fun getTokenProtoDataStore(context: Context): Flow<String?> {
        return context.protoDataStore.data.map { preferences ->
            preferences.token
        }
    }
}
object UserPreferencesSerializer : Serializer<Protodatastore.UserPreferences> {
    override suspend fun readFrom(input: InputStream): Protodatastore.UserPreferences {
        try {
            return Protodatastore.UserPreferences.parseFrom(input)
        } catch (exception: InvalidProtocolBufferException) {
            throw CorruptionException("Cannot read proto.", exception)
        }
    }

    override suspend fun writeTo(t: Protodatastore.UserPreferences, output: OutputStream) = t.writeTo(output)
    override val defaultValue: Protodatastore.UserPreferences
        get() = Protodatastore.UserPreferences.getDefaultInstance()
}