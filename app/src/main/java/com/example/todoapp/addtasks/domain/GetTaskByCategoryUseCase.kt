package com.example.todoapp.addtasks.domain

import com.example.todoapp.addtasks.data.TaskRepository
import com.example.todoapp.addtasks.domain.model.TaskItem
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetTaskByCategoryUseCase @Inject constructor(
    private val taskRepository: TaskRepository,
) {
    operator fun invoke(category: String): Flow<List<TaskItem>> {
        return taskRepository.getTasksByCategory(category)
    }
}
