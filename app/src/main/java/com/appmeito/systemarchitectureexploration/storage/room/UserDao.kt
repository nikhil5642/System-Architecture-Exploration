package com.appmeito.systemarchitectureexploration.storage.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface UserDao {
    @Insert
    suspend fun insertUser(user: User)

    @Query("SELECT * FROM users")
    fun getAllUsers(): List<User>

    @Query("DELETE FROM users")
    suspend fun deleteAllUsers()
}
