package com.example.todoapp.alarm.domain

import android.content.Context
import com.example.todoapp.services.AlarmManager
import javax.inject.Inject

class AreBasicPermissionsGrantedUseCase @Inject constructor(
    private val checkNotificationPermissionUseCase: CheckNotificationPermissionUseCase,
    private val alarmManager: AlarmManager,
) {
    operator fun invoke(context: Context): Boolean {
        val isNotificationPermissionGranted = checkNotificationPermissionUseCase(context)
        val isAlarmPermissionGranted = alarmManager.canScheduleExactAlarms()
        return isNotificationPermissionGranted && isAlarmPermissionGranted
    }
}