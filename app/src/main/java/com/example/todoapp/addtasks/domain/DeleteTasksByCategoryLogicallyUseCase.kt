package com.example.todoapp.addtasks.domain

import com.example.todoapp.addtasks.data.TaskRepository
import javax.inject.Inject

class DeleteTasksByCategoryLogicallyUseCase @Inject constructor(private val taskRepository: TaskRepository) {
    suspend operator fun invoke(categoryId: String) {
        taskRepository.deleteTasksByCategoryLogically(categoryId)
    }
}