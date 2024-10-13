package com.example.todoapp.addtasks.domain

import com.example.todoapp.addtasks.data.TaskRepository
import com.example.todoapp.addtasks.data.toDatabase
import com.example.todoapp.addtasks.domain.model.TaskItem
import com.example.todoapp.user.domain.GetUserUseCase
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class AddTaskUseCase @Inject constructor(
    private val taskRepository: TaskRepository,
    private val getUserUseCase: GetUserUseCase,
    private val handleAlarmTaskUseCase: HandleAlarmTaskUseCase,
) {
    suspend operator fun invoke(taskItem: TaskItem) {
        taskRepository.add(taskItem.toDatabase().copy(userId = getUserUseCase().first()?.uid))

        handleAlarmTaskUseCase(taskItem, null)
    }
}