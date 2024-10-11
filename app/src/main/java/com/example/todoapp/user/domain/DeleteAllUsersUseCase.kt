package com.example.todoapp.user.domain

import com.example.todoapp.user.data.UserRepository
import javax.inject.Inject

class DeleteAllUsersUseCase @Inject constructor(private val userRepository: UserRepository) {
    suspend operator fun invoke() {
        userRepository.deleteAllUsers()
    }
}