package com.example.todoapp.user.domain

import com.example.todoapp.user.data.UserRepository
import com.example.todoapp.user.domain.model.UserItem
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetUserUseCase @Inject constructor(private val userRepository: UserRepository) {
    operator fun invoke(): Flow<UserItem?> = userRepository.user
}