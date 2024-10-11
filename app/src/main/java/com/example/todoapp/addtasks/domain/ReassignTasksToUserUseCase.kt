package com.example.todoapp.addtasks.domain

import com.example.todoapp.addtasks.data.TaskRepository
import com.example.todoapp.utils.Logger
import javax.inject.Inject

class ReassignTasksToUserUseCase @Inject constructor(
    private val taskRepository: TaskRepository,
) {
    suspend operator fun invoke(userId: String?) {
        try {
            taskRepository.reassignTasksToUser(userId)
        } catch (e: Exception) {
            Logger.error("ReassignTasksToUserUseCase", e.message.orEmpty())
        }
    }
}