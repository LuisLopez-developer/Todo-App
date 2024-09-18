package com.example.todoapp.settings.auth.domain

import com.example.todoapp.settings.auth.data.AuthRepository
import com.example.todoapp.settings.auth.data.UserEntity
import javax.inject.Inject

class AddUserCaseUse @Inject constructor(private val userRepository: AuthRepository) {
    suspend operator fun invoke(user: UserEntity) {
        userRepository.add(user)
    }
}