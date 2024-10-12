package com.example.todoapp.alarm.domain

import androidx.activity.result.ActivityResultLauncher
import com.example.todoapp.services.permission.PermissionService
import javax.inject.Inject

class SetPermissionLauncherUseCase @Inject constructor(private val permissionService: PermissionService) {
    operator fun invoke(launcher: ActivityResultLauncher<String>) {
        permissionService.setPermissionLauncher(launcher)
    }
}