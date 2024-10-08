package com.example.todoapp.addtasks.domain

import com.example.todoapp.addtasks.data.TaskRepository
import com.example.todoapp.addtasks.domain.model.TaskItem
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetTaskByIdFlowUseCase @Inject constructor(private val taskRepository: TaskRepository) {
    operator fun invoke(taskId: String): Flow<TaskItem> = taskRepository.getTaskByIdFlow(taskId)
}