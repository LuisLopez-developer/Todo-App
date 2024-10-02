package com.example.todoapp.settings.auth.domain

import com.example.todoapp.settings.auth.data.AuthRepository
import javax.inject.Inject

class DeleteAllUsersUseCase @Inject constructor(private val authRepository: AuthRepository) {
    suspend operator fun invoke() {
        authRepository.deleteAllUsers()
    }
}