package com.example.todoapp.user.domain

import com.example.todoapp.user.data.UserRepository
import com.example.todoapp.user.data.toDatabase
import com.example.todoapp.user.domain.model.UserItem
import javax.inject.Inject

class AddUserCaseUse @Inject constructor(private val userRepository: UserRepository) {
    suspend operator fun invoke(user: UserItem) {
        userRepository.addUser(user.toDatabase())
    }
}