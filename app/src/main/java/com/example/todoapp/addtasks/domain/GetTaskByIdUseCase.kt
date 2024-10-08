package com.example.todoapp.addtasks.domain

import com.example.todoapp.addtasks.data.TaskRepository
import com.example.todoapp.addtasks.domain.model.TaskItem
import javax.inject.Inject

class GetTaskByIdUseCase @Inject constructor(
    private val taskRepository: TaskRepository,
) {
    suspend fun execute(taskId: String): TaskItem? {
        return taskRepository.getTaskById(taskId)
    }
}
