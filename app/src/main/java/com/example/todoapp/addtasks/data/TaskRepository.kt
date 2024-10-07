package com.example.todoapp.addtasks.data

import com.example.todoapp.addtasks.domain.model.TaskItem
import com.example.todoapp.addtasks.domain.model.toDomain
import com.example.todoapp.addtasks.ui.model.TaskModel
import com.example.todoapp.settings.auth.data.UserDao
import com.example.todoapp.state.data.constants.DefaultStateId.ACTIVE_ID
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import org.threeten.bp.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TaskRepository @Inject constructor(
    private val taskDao: TaskDao,
    private val userDao: UserDao,
) {

    val tasks: Flow<List<TaskModel>> = taskDao.getActiveTasks().map { it.toTaskModelList() }

    val allTasks: Flow<List<TaskItem>> = taskDao.getTasks().map { item ->
        item.map { it.toDomain() }
    }

    suspend fun add(taskModel: TaskModel) {
        taskDao.addTask(
            taskModel.toData()
                .copy(userId = userDao.getUser().first()?.uid, stateId = ACTIVE_ID)
        )
    }

    suspend fun update(taskModel: TaskModel) {
        taskDao.updateTask(taskModel.toData())
    }

    suspend fun delete(taskModel: TaskModel) {
        taskDao.deleteTask(taskModel.toData())
    }

    suspend fun deleteTasksByCategoryLogically(categoryId: String) {
        taskDao.deleteTasksByCategoryLogically(categoryId)
    }

    suspend fun getTaskById(taskId: String): TaskModel? {
        val taskEntity = taskDao.getTaskById(taskId)
        return taskEntity?.toTaskModel()
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

// Extensión para convertir TaskModel a TaskEntity, que es la representación de la base de datos.
fun TaskModel.toData(): TaskEntity {
    return TaskEntity(
        this.id,
        this.task,
        this.selected,
        this.startDate,
        this.endDate,
        this.time,
        this.details,
        this.categoryId,
        stateId = this.stateId,
        createdAt = this.createdAt,
        updatedAt = this.updatedAt
    )
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