package com.example.todoapp.settings.auth.domain

import androidx.credentials.GetCredentialResponse
import com.example.todoapp.settings.auth.data.AuthRepository
import javax.inject.Inject

class HandleSignInUseCase @Inject constructor(private val authRepository: AuthRepository) {
    operator fun invoke(credentialResponse: GetCredentialResponse) {
        authRepository.handleSignIn(credentialResponse)
    }
}