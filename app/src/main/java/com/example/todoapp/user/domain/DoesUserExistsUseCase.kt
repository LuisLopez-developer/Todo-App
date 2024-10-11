package com.example.todoapp.user.domain

import com.example.todoapp.user.data.UserRepository
import javax.inject.Inject

class DoesUserExistsUseCase @Inject constructor(private val userRepository: UserRepository) {
    suspend operator fun invoke(userId: String): Boolean {
        return userRepository.doesUserExists(userId)
    }
}