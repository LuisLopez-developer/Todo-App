package com.example.todoapp.alarm.domain

import com.example.todoapp.services.permission.PermissionService
import javax.inject.Inject

class RequestExactAlarmPermissionUseCase @Inject constructor(private val permissionService: PermissionService) {
    operator fun invoke() {
        permissionService.requestExactAlarmPermission()
    }
}