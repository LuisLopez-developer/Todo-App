package com.example.todoapp.addtasks.domain

import com.example.todoapp.addtasks.data.TaskRepository
import javax.inject.Inject

class GetAllTasksUseCase @Inject constructor(private val taskRepository: TaskRepository) {
    operator fun invoke() = taskRepository.allTasks
}