package com.example.todoapp.user.data

import com.example.todoapp.user.domain.model.UserItem
import com.example.todoapp.user.domain.model.toDomain
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepository @Inject constructor(private val userDao: UserDao) {

        val user: Flow<UserItem?> = userDao.getUser().map { it?.toDomain() }

        suspend fun addUser(user: UserEntity) {
            userDao.addUser(user)
        }

        suspend fun deleteAllUsers() {
            userDao.deleteAllUsers()
        }

        suspend fun doesUserExists(userId: String): Boolean {
            return userDao.doesUserExists(userId)
        }

}