package com.example.todoapp.alarm.ui

import android.content.Context
import androidx.activity.result.ActivityResultLauncher
import androidx.lifecycle.ViewModel
import com.example.todoapp.services.AlarmManager
import com.example.todoapp.services.permission.PermissionService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class AlarmViewModel @Inject constructor(
    private val alarmManager: AlarmManager,
    private val permissionService: PermissionService,
) : ViewModel() {

    private val _alarmPermissionGranted = MutableStateFlow(false)
    val alarmPermissionGranted: StateFlow<Boolean> = _alarmPermissionGranted

    private val _postNotificationPermissionGranted = MutableStateFlow(false)
    val postNotificationPermissionGranted: StateFlow<Boolean> = _postNotificationPermissionGranted

    init {
        canScheduleExactAlarms()
    }

    fun openAppSettings(context: Context) {
        permissionService.openAppSettings(context)
    }

    fun setPermissionLauncher(launcher: ActivityResultLauncher<String>) {
        permissionService.setPermissionLauncher(launcher)
    }

    fun checkNotificationPermission(context: Context) {
        _postNotificationPermissionGranted.value =
            permissionService.isNotificationPermissionGranted(context)

        if (!_postNotificationPermissionGranted.value) {
            // Solicitar permiso si no está concedido
            permissionService.requestNotificationPermission()
        }
    }

    fun canScheduleExactAlarms() {
        _alarmPermissionGranted.value = !alarmManager.canScheduleExactAlarms()
    }

    fun requestExactAlarmPermission() {
        alarmManager.requestExactAlarmPermission()
    }

}