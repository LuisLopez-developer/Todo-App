package com.example.todoapp.addtasks.data

import com.example.todoapp.addtasks.domain.model.TaskItem
import com.example.todoapp.addtasks.domain.model.toDomain
import com.example.todoapp.addtasks.domain.model.toDomainList
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.threeten.bp.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TaskRepository @Inject constructor(private val taskDao: TaskDao) {

    val tasks: Flow<List<TaskItem>> = taskDao.getActiveTasks().map { it.toDomainList() }

    val allTasks: Flow<List<TaskItem>> = taskDao.getTasks().map { it.toDomainList() }

    suspend fun add(taskEntity: TaskEntity) {
        taskDao.addTask(taskEntity)
    }

    suspend fun update(taskEntity: TaskEntity) {
        taskDao.updateTask(taskEntity)
    }

    suspend fun delete(taskEntity: TaskEntity) {
        taskDao.deleteTask(taskEntity)
    }

    suspend fun deleteTasksByCategoryLogically(categoryId: String) {
        taskDao.deleteTasksByCategoryLogically(categoryId)
    }

    suspend fun getTaskById(taskId: String): TaskItem? {
        return taskDao.getTaskById(taskId)?.toDomain()
    }

    fun getTaskByIdFlow(taskId: String): Flow<TaskItem> {
        return taskDao.getTaskByIdFlow(taskId).map { it.toDomain() }
    }

    fun getTasksByCategory(categoryId: String): Flow<List<TaskItem>> {
        return taskDao.getTasksByCategory(categoryId).map { items -> items.toDomainList() }
    }

    fun getTasksByDateFlow(date: LocalDate): Flow<List<TaskItem>> {
        val dateString = date.format(org.threeten.bp.format.DateTimeFormatter.ISO_LOCAL_DATE)
        return taskDao.getActiveTasksByDate(dateString).map { it.toDomainList() }
    }
}