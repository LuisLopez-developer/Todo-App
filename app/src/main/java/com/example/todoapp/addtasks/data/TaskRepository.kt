package com.example.todoapp.addtasks.data

import com.example.todoapp.addtasks.domain.model.TaskItem
import com.example.todoapp.addtasks.domain.model.toDomain
import com.example.todoapp.addtasks.domain.model.toDomainList
import com.example.todoapp.addtasks.ui.model.TaskModel
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
        val taskEntity = taskDao.getTaskById(taskId)
        return taskEntity?.toDomain()
    }

    fun getTaskByIdFlow(taskId: String): Flow<TaskModel> {
        return taskDao.getTaskByIdFlow(taskId).map { it.toTaskModel() }
    }

    fun getTasksByCategory(categoryId: String): Flow<List<TaskModel>> {
        return taskDao.getTasksByCategory(categoryId).map { items -> items.toTaskModelList() }
    }

    fun getTasksByDateFlow(date: LocalDate): Flow<List<TaskModel>> {
        val dateString = date.format(org.threeten.bp.format.DateTimeFormatter.ISO_LOCAL_DATE)
        return taskDao.getActiveTasksByDate(dateString).map { it.toTaskModelList() }
    }
}

// Extensión para convertir TaskEntity a TaskModel, que es la representación usada en la UI
fun TaskEntity.toTaskModel(): TaskModel {
    return TaskModel(
        id = this.id,
        task = this.task,
        selected = this.selected,
        startDate = this.startDate,
        endDate = this.endDate,
        time = this.time,
        details = this.details,
        categoryId = this.categoryId,
        stateId = this.stateId
    )
}

// Extensión para convertir una lista de TaskEntity a una lista de TaskModel.
// Usa map para aplicar la conversión a cada elemento de la lista.
fun List<TaskEntity>.toTaskModelList(): List<TaskModel> = this.map { it.toTaskModel() }