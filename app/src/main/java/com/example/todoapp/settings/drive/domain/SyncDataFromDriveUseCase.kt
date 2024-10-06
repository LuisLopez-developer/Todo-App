package com.example.todoapp.settings.drive.domain


import android.util.Log
import com.example.todoapp.addtasks.data.TaskEntity
import com.example.todoapp.addtasks.data.toTaskModel
import com.example.todoapp.addtasks.domain.AddTaskUseCase
import com.example.todoapp.addtasks.domain.GetTaskByIdUseCase
import com.example.todoapp.addtasks.ui.model.TaskModel
import com.example.todoapp.settings.auth.domain.DoesUserExistsUseCase
import com.example.todoapp.settings.drive.data.GoogleDriveRepository
import com.example.todoapp.taskcategory.data.CategoryEntity
import com.example.todoapp.taskcategory.data.toCategoryModel
import com.example.todoapp.taskcategory.domain.AddCategoryUseCase
import com.example.todoapp.taskcategory.domain.GetCategoryByIdUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class SyncDataFromDriveUseCase @Inject constructor(
    private val driveRepository: GoogleDriveRepository,
    private val addTaskUseCase: AddTaskUseCase,
    private val getTaskByIdUseCase: GetTaskByIdUseCase,
    private val addCategoryUseCase: AddCategoryUseCase,
    private val getCategoryByIdUseCase: GetCategoryByIdUseCase,
    private val doesUserExistsUseCase: DoesUserExistsUseCase,
) {
    suspend operator fun invoke(accessToken: String) = withContext(Dispatchers.IO) {
        val driveService = driveRepository.getDrive(accessToken)
        handleCategories(
            driveRepository.retrieveFilesFromDrive(
                driveService,
                "category",
                CategoryEntity::class.java
            )
        )
        handleTasks(
            driveRepository.retrieveFilesFromDrive(
                driveService,
                "task",
                TaskEntity::class.java
            )
        )
    }

    private suspend fun handleTasks(tasks: List<TaskEntity>) {
        tasks.forEach { taskEntity ->
            try {
                val existingTask: TaskModel? =
                    getTaskByIdUseCase.execute(taskEntity.toTaskModel().id)
                if (existingTask == null) {
                    addTaskUseCase(taskEntity.toTaskModel())
                    logMessage("Tarea añadida: ${taskEntity.task}")
                } else if (taskEntity.updatedAt.isAfter(existingTask.updatedAt)) {
                    addTaskUseCase(taskEntity.toTaskModel())
                    logMessage("Tarea actualizada: ${taskEntity.task}")
                } else {
                    logMessage("La tarea ya existe y está actualizada: ${taskEntity.task}")
                }
            } catch (e: Exception) {
                logError("Error al manejar tarea: ${taskEntity.task}", e)
            }
        }
    }

    private suspend fun handleCategories(categories: List<CategoryEntity>) {
        categories.forEach { categoryEntity ->
            try {
                if (categoryEntity.userId != null && !doesUserExistsUseCase(categoryEntity.userId)) {
                    logError("Invalid userId: ${categoryEntity.userId}")
                    return@forEach
                }
                val existingCategory = getCategoryByIdUseCase.invoke(categoryEntity.id)
                if (existingCategory == null) {
                    addCategoryUseCase(categoryEntity.toCategoryModel())
                    logMessage("Categoria añadida: ${categoryEntity.category}")
                } else if (categoryEntity.updatedAt.isAfter(existingCategory.updatedAt)) {
                    addCategoryUseCase(categoryEntity.toCategoryModel())
                    logMessage("Categoria actualizada: ${categoryEntity.category}")
                } else {
                    logMessage("La categoría ya existe y está actualizada: ${categoryEntity.category}")
                }
            } catch (e: Exception) {
                logError("Error al manejar categoría: ${categoryEntity.category}", e)
            }
        }
    }

    private fun logMessage(message: String) {
        Log.d("SyncDataFromDriveUseCase", message)
    }

    private fun logError(message: String, e: Exception? = null) {
        Log.e("SyncDataFromDriveUseCase", message, e)
    }
}