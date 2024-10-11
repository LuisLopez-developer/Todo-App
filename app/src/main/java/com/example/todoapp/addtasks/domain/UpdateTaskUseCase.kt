package com.example.todoapp.addtasks.domain

import com.example.todoapp.addtasks.data.TaskRepository
import com.example.todoapp.addtasks.data.toDatabase
import com.example.todoapp.addtasks.domain.model.TaskItem
import javax.inject.Inject

class UpdateTaskUseCase @Inject constructor(
    private val taskRepository: TaskRepository,
    private val alarmTaskUseCase: HandleAlarmTaskUseCase,
    private val getTaskByIdUseCase: GetTaskByIdUseCase,
) {
    suspend operator fun invoke(taskItem: TaskItem) {
        // Obtener la tarea original antes de actualizarla
        val originalTask = getTaskByIdUseCase.execute(taskItem.id)

        // Actualizar la tarea en el repositorio
        taskRepository.update(taskItem.toDatabase())

        // Manejar alarmas
        alarmTaskUseCase(taskItem, originalTask)
    }
}