package com.example.todoapp.settings.auth.domain

import com.example.todoapp.settings.auth.data.AuthRepository
import com.example.todoapp.settings.auth.ui.model.UserModel
import kotlinx.coroutines.flow.Flow

import javax.inject.Inject

class GetUserUseCase @Inject constructor(private val authRepository: AuthRepository) {
    operator fun invoke(): Flow<UserModel?> = authRepository.user
}