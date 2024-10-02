package com.example.todoapp.settings.auth.domain

import com.example.todoapp.settings.auth.data.AuthRepository
import javax.inject.Inject

class SignOutUseCase @Inject constructor(private val authRepository: AuthRepository) {
    operator fun invoke() {
        authRepository.signOut()
    }
}