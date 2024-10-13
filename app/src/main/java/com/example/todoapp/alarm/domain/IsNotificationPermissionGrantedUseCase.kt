package com.example.todoapp.alarm.domain

import javax.inject.Inject

class IsNotificationPermissionGrantedUseCase @Inject constructor(private val checkNotificationPermissionUseCase: CheckNotificationPermissionUseCase) {
    operator fun invoke(): Boolean {
        return checkNotificationPermissionUseCase()
    }
}