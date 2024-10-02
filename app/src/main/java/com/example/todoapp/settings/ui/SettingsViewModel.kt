package com.example.todoapp.settings.ui

import androidx.credentials.GetCredentialResponse
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.todoapp.settings.auth.domain.GetUserUseCase
import com.example.todoapp.settings.auth.domain.HandleSignInUseCase
import com.example.todoapp.settings.firestore.domain.SyncDataFromFirestoreUseCase
import com.example.todoapp.settings.firestore.domain.SyncDataWithFirebaseUseCase
import com.example.todoapp.settings.ui.UserUiState.Success
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val syncDataFromFirestoreUseCase: SyncDataFromFirestoreUseCase,
    private val syncDataWithFirestoreUseCase: SyncDataWithFirebaseUseCase,
    private val handleSignInUseCase: HandleSignInUseCase,
    getUserUseCase: GetUserUseCase,
) : ViewModel() {

    val userUiState: StateFlow<UserUiState> =
        getUserUseCase().map {
            if (it != null) {
                Success(it)
            } else {
                UserUiState.Empty
            }
        }
            .catch { Throwable(it) }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), UserUiState.Loading)

    fun handleSignIn(result: GetCredentialResponse) {
        handleSignInUseCase(result)
    }

    fun syncTasks() {
        viewModelScope.launch {
            syncDataWithFirestoreUseCase()
        }
    }

    fun syncTasksFromFirebase() {
        viewModelScope.launch {
            syncDataFromFirestoreUseCase()
        }
    }
}