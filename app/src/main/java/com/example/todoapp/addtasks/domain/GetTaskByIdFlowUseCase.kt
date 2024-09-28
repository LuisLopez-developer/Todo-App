package com.example.todoapp.addtasks.domain

import com.example.todoapp.addtasks.data.TaskRepository
import javax.inject.Inject

class GetTaskByIdFlowUseCase @Inject constructor(private val taskRepository: TaskRepository) {
    operator fun invoke(taskId: String) = taskRepository.getTaskByIdFlow(taskId)
}