package com.example.todoapp.addtasks.data

import com.example.todoapp.addtasks.ui.model.TaskModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.threeten.bp.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TaskRepository @Inject constructor(private val taskDao: TaskDao) {

    val tasks: Flow<List<TaskModel>> = taskDao.getTasks().map { items ->
        items.map {
            TaskModel(it.id, it.task, it.selected, it.startDate, it.endDate, it.time, it.details, it.category)
        }
    }

    suspend fun add(taskModel: TaskModel) {
        taskDao.addTask(taskModel.toData())
    }

    suspend fun update(taskModel: TaskModel) {
        taskDao.updateTask(taskModel.toData())
    }

    suspend fun delete(taskModel: TaskModel) {
        taskDao.deleteTask(taskModel.toData())
    }

    suspend fun getTaskById(taskId: Int): TaskModel? {
        val taskEntity = taskDao.getTaskById(taskId)
        return taskEntity?.let {
            TaskModel(it.id, it.task, it.selected, it.startDate, it.endDate, it.time, it.details, it.category)
        }
    }

    fun getTasksByDateFlow(date: LocalDate): Flow<List<TaskModel>> {
        val dateString = date.format(org.threeten.bp.format.DateTimeFormatter.ISO_LOCAL_DATE)
        return taskDao.getTasksByDate(dateString).map { items ->
            items.map {
                TaskModel(
                    it.id,
                    it.task,
                    it.selected,
                    it.startDate,
                    it.endDate,
                    it.time,
                    it.details,
                    it.category
                )
            }
        }
    }
}

fun TaskModel.toData(): TaskEntity {
    return TaskEntity(
        this.id,
        this.task,
        this.selected,
        this.startDate,
        this.endDate,
        this.time,
        this.details,
        this.category
    )
}
