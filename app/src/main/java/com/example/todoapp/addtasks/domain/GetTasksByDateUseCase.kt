package com.example.todoapp.addtasks.domain

import com.example.todoapp.addtasks.data.TaskRepository
import com.example.todoapp.addtasks.ui.model.TaskModel
import kotlinx.coroutines.flow.Flow
import org.threeten.bp.LocalDate
import javax.inject.Inject

class GetTasksByDateUseCase @Inject constructor(private val taskRepository: TaskRepository) {
    operator fun invoke(date: LocalDate): Flow<List<TaskModel>> =
        taskRepository.getTasksByDateFlow(date)
}