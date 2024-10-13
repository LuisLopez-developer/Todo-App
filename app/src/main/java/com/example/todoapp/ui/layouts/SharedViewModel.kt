package com.example.todoapp.ui.layouts

import android.content.Context
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.compose.foundation.layout.RowScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.todoapp.alarm.domain.AreBasicPermissionsGrantedUseCase
import com.example.todoapp.alarm.domain.CheckNotificationPermissionUseCase
import com.example.todoapp.alarm.domain.IsNotificationPermissionGrantedUseCase
import com.example.todoapp.alarm.domain.OpenAppSettingsUseCase
import com.example.todoapp.alarm.domain.RequestExactAlarmPermissionUseCase
import com.example.todoapp.alarm.domain.RequestNotificationPermissionUseCase
import com.example.todoapp.alarm.domain.SetPermissionLauncherUseCase
import com.example.todoapp.services.AlarmManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class SharedViewModel @Inject constructor(
    private val alarmManager: AlarmManager, // Inyecta cualquier dependencia si es necesario
    private val checkNotificationPermissionUseCase: CheckNotificationPermissionUseCase,
    private val openAppSettingsUseCase: OpenAppSettingsUseCase,
    private val requestExactAlarmPermissionUseCase: RequestExactAlarmPermissionUseCase,
    private val areBasicPermissionsGrantedUseCase: AreBasicPermissionsGrantedUseCase,
    private val isNotificationPermissionGrantedUseCase: IsNotificationPermissionGrantedUseCase,
    private val requestNotificationPermissionUseCase: RequestNotificationPermissionUseCase,
    private val setPermissionLauncherUseCase: SetPermissionLauncherUseCase,
) : ViewModel() {
    // Estado para manejar el título de la top bar y otros componentes comunes
    val topBarTitle = mutableStateOf("Todo App")
    val topBarActions = mutableStateOf<@Composable RowScope.() -> Unit>({})
    val topBarNavigationIcon = mutableStateOf<@Composable () -> Unit>({})

    // Estados de los permisos
    private val _permissionsGranted = MutableStateFlow(false)
    val permissionsGranted: StateFlow<Boolean> = _permissionsGranted

    private val _alarmPermissionGranted = MutableStateFlow(false)
    val alarmPermissionGranted: StateFlow<Boolean> = _alarmPermissionGranted

    private val _postNotificationPermissionGranted = MutableStateFlow(false)
    val postNotificationPermissionGranted: StateFlow<Boolean> = _postNotificationPermissionGranted

    // Función para verificar los permisos
    fun checkPermissions() {
        _postNotificationPermissionGranted.value = checkNotificationPermissionUseCase()
        _alarmPermissionGranted.value = alarmManager.canScheduleExactAlarms()
    }

    // Función para solicitar permiso de alarma exacta
    fun requestExactAlarmPermission() {
        requestExactAlarmPermissionUseCase()
    }

    // Función para abrir la configuración de la app
    fun openAppSettings(context: Context) {
        openAppSettingsUseCase(context)
    }

    fun setPermissionLauncher(permissionLauncher: ManagedActivityResultLauncher<String, Boolean>) {
        setPermissionLauncherUseCase(permissionLauncher)
    }

    fun isNotificationPermissionGranted(): Boolean {
        return isNotificationPermissionGrantedUseCase()
    }

    fun requestNotificationPermission() {
        requestNotificationPermissionUseCase()
    }

    fun areBasicPermissionsGranted(): Boolean {
        return areBasicPermissionsGrantedUseCase()
    }
}