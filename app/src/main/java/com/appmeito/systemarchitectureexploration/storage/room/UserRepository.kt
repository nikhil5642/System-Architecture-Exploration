package com.appmeito.systemarchitectureexploration.storage.room


class UserRepository(private val userDao: UserDao) {
    suspend fun insertUser(user: User) = userDao.insertUser(user)
    fun getAllUsers(): List<User> = userDao.getAllUsers()
    suspend fun deleteAllUsers() = userDao.deleteAllUsers()
}
