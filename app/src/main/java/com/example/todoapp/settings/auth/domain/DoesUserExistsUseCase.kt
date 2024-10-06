package com.example.todoapp.settings.auth.domain

import com.example.todoapp.settings.auth.data.AuthRepository
import javax.inject.Inject

class DoesUserExistsUseCase @Inject constructor(private val userRepository: AuthRepository) {
    suspend operator fun invoke(userId: String): Boolean {
        return userRepository.doesUserExists(userId)
    }
}