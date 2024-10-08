package com.example.todoapp.addtasks.domain

import com.example.todoapp.addtasks.data.TaskRepository
import com.example.todoapp.addtasks.data.toDatabase
import com.example.todoapp.addtasks.domain.model.TaskItem
import com.example.todoapp.state.data.constants.DefaultStateId.DELETED_ID
import javax.inject.Inject

class DeleteTaskUseCase @Inject constructor(private val taskRepository: TaskRepository) {
    suspend operator fun invoke(taskItem: TaskItem) {
        val newCategoryName = "${taskItem.task}_${System.currentTimeMillis()}"
        taskRepository.update(
            taskItem.copy(task = newCategoryName, stateId = DELETED_ID).toDatabase()
        )
    }
}