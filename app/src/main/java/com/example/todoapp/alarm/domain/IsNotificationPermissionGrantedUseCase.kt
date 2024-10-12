package com.example.todoapp.alarm.domain

import android.content.Context
import javax.inject.Inject

class IsNotificationPermissionGrantedUseCase @Inject constructor(private val checkNotificationPermissionUseCase: CheckNotificationPermissionUseCase) {
    operator fun invoke(context: Context): Boolean {
        return checkNotificationPermissionUseCase(context)
    }
}