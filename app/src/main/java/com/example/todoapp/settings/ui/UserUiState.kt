package com.example.todoapp.settings.ui

import com.example.todoapp.settings.auth.ui.model.UserModel

sealed interface UserUiState {
    data object Loading : UserUiState
    data class Success(val user: UserModel) : UserUiState
    data class Error(val throwable: Throwable) : UserUiState
    data object Empty : UserUiState
}