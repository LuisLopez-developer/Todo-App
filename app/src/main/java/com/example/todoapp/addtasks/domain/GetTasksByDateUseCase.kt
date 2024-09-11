package com.example.todoapp.addtasks.domain

import com.example.todoapp.addtasks.data.TaskRepository
import com.example.todoapp.addtasks.ui.model.TaskModel
import org.threeten.bp.LocalDate
import javax.inject.Inject

class GetTasksByDateUseCase @Inject constructor(private val taskRepository: TaskRepository) {
    suspend operator fun invoke(date: LocalDate): List<TaskModel> {
        return taskRepository.getTasksByDate(date)
    }
}
