package com.example.todoapp.settings.drive.ui

import android.content.Context
import android.widget.Toast
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

    fun setUserId(userId: String) {
        _userId.value = userId
    }

    private val _totalFiles = MutableStateFlow(0)

    private val _expandedAlert = MutableStateFlow(false)
    val expandedAlert: StateFlow<Boolean> = _expandedAlert

    fun onExpandedAlert() {
        _expandedAlert.value = !_expandedAlert.value
    }

    private val _syncAutoChecked = MutableStateFlow(false)
    val syncAutoChecked: StateFlow<Boolean> = _syncAutoChecked

    fun onSyncAutoChecked() {
        _syncAutoChecked.value = !_syncAutoChecked.value
    }

    fun syncDataWith(accessToken: String) {
        viewModelScope.launch {
            syncDataWithDriveUseCase(accessToken)
        }
    }

    fun syncDataFrom(accessToken: String) {
        viewModelScope.launch {
            syncDataFromDriveUseCase(accessToken)
        }
    }

    fun clearAppDataFromGoogleDrive(accessToken: String) {
        viewModelScope.launch {
            clearAppDataFromGoogleDriveUseCase(accessToken)
            onExpandedAlert()
        }
    }

    fun getTotalFileNumbers(accessToken: String, context: Context) {
        viewModelScope.launch {
            _totalFiles.value = countFilesInDriveUseCase(accessToken)
            Toast.makeText(
                context,
                "Total de archivos: ${_totalFiles.value}, Eliminación exitosa",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

}