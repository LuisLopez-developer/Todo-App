package com.example.todoapp.settings.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.todoapp.settings.auth.domain.SignOutUseCase
import com.example.todoapp.settings.ui.UserUiState.Success
import com.example.todoapp.user.domain.GetUserUseCase
import com.example.todoapp.user.ui.model.toViewModel
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
    private val signOutUseCase: SignOutUseCase,
    getUserUseCase: GetUserUseCase,
) : ViewModel() {

    val userUiState: StateFlow<UserUiState> =
        getUserUseCase().map {
            if (it != null) {
                Success(it.toViewModel())
            } else {
                UserUiState.Empty
            }
        }
            .catch { Throwable(it) }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), UserUiState.Loading)


    fun signOut() {
        viewModelScope.launch {
            signOutUseCase()
        }
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
}