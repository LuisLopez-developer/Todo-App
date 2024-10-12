package com.example.todoapp.alarm.ui

import androidx.lifecycle.ViewModel
import com.example.todoapp.services.AlarmManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class AlarmViewModel @Inject constructor(private val alarmManager: AlarmManager) : ViewModel() {

    private val _alarmPermissionGranted = MutableStateFlow(false)
    val alarmPermissionGranted: StateFlow<Boolean> = _alarmPermissionGranted

    private val _postNotificationPermissionGranted = MutableStateFlow(false)
    val postNotificationPermissionGranted: StateFlow<Boolean> = _postNotificationPermissionGranted


    init {
        canScheduleExactAlarms()
    }

    fun canScheduleExactAlarms() {
        _alarmPermissionGranted.value = !alarmManager.canScheduleExactAlarms()
    }

    fun requestExactAlarmPermission() {
        alarmManager.requestExactAlarmPermission()
    }

}