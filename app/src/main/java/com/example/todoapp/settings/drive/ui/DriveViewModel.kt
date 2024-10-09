package com.example.todoapp.settings.drive.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.todoapp.settings.drive.domain.ClearAppDataFromGoogleDriveUseCase
import com.example.todoapp.settings.drive.domain.CountFilesInDriveUseCase
import com.example.todoapp.settings.drive.domain.SyncDataFromDriveUseCase
import com.example.todoapp.settings.drive.domain.SyncDataWithDriveUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DriveViewModel @Inject constructor(
    private val syncDataWithDriveUseCase: SyncDataWithDriveUseCase,
    private val syncDataFromDriveUseCase: SyncDataFromDriveUseCase,
    private val clearAppDataFromGoogleDriveUseCase: ClearAppDataFromGoogleDriveUseCase,
    private val countFilesInDriveUseCase: CountFilesInDriveUseCase,
) : ViewModel() {

    private val _userId = MutableStateFlow<String?>(null)
    val userId: StateFlow<String?> = _userId

    fun setUserId(userId: String){
        _userId.value = userId
    }

    fun syncDataWith(accessToken: String){
        viewModelScope.launch {
            syncDataWithDriveUseCase(accessToken)
        }
    }

    fun syncDataFrom(accessToken: String){
        viewModelScope.launch {
            syncDataFromDriveUseCase(accessToken)
        }
    }

    fun clearAppDataFromGoogleDrive(accessToken: String){
        viewModelScope.launch {
            clearAppDataFromGoogleDriveUseCase(accessToken)
        }
    }

    fun getTotalFileNumbers(accessToken: String){
        viewModelScope.launch {
            countFilesInDriveUseCase(accessToken)
        }
    }

}