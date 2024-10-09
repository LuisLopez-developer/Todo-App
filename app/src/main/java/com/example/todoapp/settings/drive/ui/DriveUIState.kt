package com.example.todoapp.settings.drive.ui

sealed interface DriveUIState{
    data class Loading(val message: String) : DriveUIState
    data class Success(val message: String) : DriveUIState
    data class Error(val message: String) : DriveUIState
}