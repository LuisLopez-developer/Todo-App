package com.example.todoapp.alarm.domain

import com.example.todoapp.services.AlarmManager
import javax.inject.Inject

class AreBasicPermissionsGrantedUseCase @Inject constructor(
    private val checkNotificationPermissionUseCase: CheckNotificationPermissionUseCase,
    private val alarmManager: AlarmManager,
) {
    operator fun invoke(): Boolean {
        val isNotificationPermissionGranted = checkNotificationPermissionUseCase()
        val isAlarmPermissionGranted = alarmManager.canScheduleExactAlarms()
        return isNotificationPermissionGranted && isAlarmPermissionGranted
    }
}