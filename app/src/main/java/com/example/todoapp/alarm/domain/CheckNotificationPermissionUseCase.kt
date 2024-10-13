package com.example.todoapp.alarm.domain

import com.example.todoapp.services.permission.PermissionService
import javax.inject.Inject

class CheckNotificationPermissionUseCase @Inject constructor(private val permissionService: PermissionService) {
    operator fun invoke(): Boolean {
        return permissionService.isNotificationPermissionGranted()
    }
}