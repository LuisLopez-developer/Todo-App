package com.example.todoapp.alarm.ui

import android.content.Context
import androidx.lifecycle.ViewModel
import com.example.todoapp.alarm.domain.CheckNotificationPermissionUseCase
import com.example.todoapp.alarm.domain.OpenAppSettingsUseCase
import com.example.todoapp.services.AlarmManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class AlarmViewModel @Inject constructor(
    private val alarmManager: AlarmManager,
    private val checkNotificationPermissionUseCase: CheckNotificationPermissionUseCase,
    private val openAppSettingsUseCase: OpenAppSettingsUseCase,
) : ViewModel() {

    private val _alarmPermissionGranted = MutableStateFlow(false)
    val alarmPermissionGranted: StateFlow<Boolean> = _alarmPermissionGranted

    private val _postNotificationPermissionGranted = MutableStateFlow(false)
    val postNotificationPermissionGranted: StateFlow<Boolean> = _postNotificationPermissionGranted

    init {
        canScheduleExactAlarms()
    }

    fun openAppSettings(context: Context) {
        openAppSettingsUseCase(context)
    }


    fun checkNotificationPermission(context: Context) {
        _postNotificationPermissionGranted.value = checkNotificationPermissionUseCase(context)
    }

    fun canScheduleExactAlarms() {
        _alarmPermissionGranted.value = !alarmManager.canScheduleExactAlarms()
    }

    fun requestExactAlarmPermission() {
        alarmManager.requestExactAlarmPermission()
    }

}