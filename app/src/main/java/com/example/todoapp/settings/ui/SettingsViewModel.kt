package com.example.todoapp.settings.ui

import androidx.credentials.GetCredentialResponse
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.todoapp.settings.auth.domain.GetUserUseCase
import com.example.todoapp.settings.auth.domain.HandleSignInUseCase
import com.example.todoapp.settings.auth.domain.SignOutUseCase
import com.example.todoapp.settings.firestore.domain.SyncDataFromFirestoreUseCase
import com.example.todoapp.settings.firestore.domain.SyncDataWithFirebaseUseCase
import com.example.todoapp.settings.ui.UserUiState.Success
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
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
    private val signOutUseCase: SignOutUseCase,
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

    // Lo relacionado al Auth
    fun handleSignIn(result: GetCredentialResponse) {
        handleSignInUseCase(result)
    }

    fun signOut() {
        signOutUseCase()
        onHideDropDownExpanded()
    }

    private val _dropDownExpanded = MutableStateFlow(false)
    val dropDownExpanded: StateFlow<Boolean> = _dropDownExpanded

    fun onShowDropDownExpanded() {
        _dropDownExpanded.value = true
    }

    fun onHideDropDownExpanded() {
        _dropDownExpanded.value = false
    }

    // Lo relacionado a Firestore
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