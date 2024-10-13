package com.example.todoapp.addtasks.domain

import com.example.todoapp.addtasks.data.TaskRepository
import com.example.todoapp.alarm.domain.AreBasicPermissionsGrantedUseCase
import com.example.todoapp.utils.Logger
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class RecreateAlarmsForTasksUseCase @Inject constructor(
    private val handleAlarmTaskUseCase: HandleAlarmTaskUseCase,
    private val taskRepository: TaskRepository,
    private val areBasicPermissionsGrantedUseCase: AreBasicPermissionsGrantedUseCase,
) {
    suspend operator fun invoke() {
        if (!areBasicPermissionsGrantedUseCase()) {
            Logger.debug("RecreateAlarmsForTasksUseCase", "No cuentas con los permisos basicos")
            return
        }

        // recupera todas las tareas (Activas) y crea las alarmas
        taskRepository.tasks.first().forEach { task ->
            handleAlarmTaskUseCase(task, null)
            Logger.debug("RecreateAlarmsForTasksUseCase", "Tarea: ${task.id}, alarma creada")
        }
    }
}