package com.example.todoapp.settings.drive.domain


import android.util.Log
import com.example.todoapp.addtasks.data.TaskEntity
import com.example.todoapp.addtasks.domain.AddTaskUseCase
import com.example.todoapp.addtasks.domain.GetTaskByIdUseCase
import com.example.todoapp.addtasks.domain.UpdateTaskUseCase
import com.example.todoapp.addtasks.domain.model.toDomain
import com.example.todoapp.core.NetWorkService
import com.example.todoapp.settings.drive.data.GoogleDriveRepository
import com.example.todoapp.settings.utils.toJson
import com.example.todoapp.taskcategory.data.CategoryEntity
import com.example.todoapp.taskcategory.domain.AddCategoryUseCase
import com.example.todoapp.taskcategory.domain.GetCategoryByIdUseCase
import com.example.todoapp.taskcategory.domain.UpdateCategoryUseCase
import com.example.todoapp.taskcategory.domain.model.toDomain
import com.example.todoapp.utils.Logger
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class SyncDataFromDriveUseCase @Inject constructor(
    private val driveRepository: GoogleDriveRepository,
    private val addTaskUseCase: AddTaskUseCase,
    private val getTaskByIdUseCase: GetTaskByIdUseCase,
    private val updateTaskUseCase: UpdateTaskUseCase,
    private val addCategoryUseCase: AddCategoryUseCase,
    private val updateCategoryUseCase: UpdateCategoryUseCase,
    private val getCategoryByIdUseCase: GetCategoryByIdUseCase,
    private val netWorkService: NetWorkService
) {
    suspend operator fun invoke(accessToken: String) = withContext(Dispatchers.IO) {
        if(!netWorkService.getNetworkService()) {
            return@withContext
        }

        val driveService = driveRepository.getDrive(accessToken)
        val (tasks, categories) = driveRepository.getAllTasksAndCategories(driveService)

        categories.forEach { categoryEntity ->
            handleCategory(categoryEntity)
        }

        tasks.forEach { taskEntity ->
            handleTask(taskEntity)
        }
    }

    private suspend fun handleCategory(categoryEntity: CategoryEntity) {
        val localCategory = getCategoryByIdUseCase(categoryEntity.id)

        if (localCategory == null) {
            // Si la categoría no existe en la base de datos local, agregarla
            Logger.debug(
                "SyncDataFromDriveUseCase",
                "Agregando nueva categoría desde Google Drive: ${categoryEntity.toJson()}"
            )
            addCategoryUseCase(categoryEntity.toDomain())
        } else {
            // Validar si el `updatedAt` de Google Drive es más reciente antes de actualizar
            if (categoryEntity.updatedAt.isAfter(localCategory.updatedAt)) {
                Logger.debug(
                    "SyncDataFromDriveUseCase",
                    "Actualizando categoría en base de datos local: ${categoryEntity.toJson()}"
                )
                updateCategoryUseCase(categoryEntity.toDomain())
            } else {
                Log.d(
                    "SyncDataFromDriveUseCase",
                    "La categoría en Google Drive no es más reciente, no se actualiza."
                )
            }
        }
    }

    private suspend fun handleTask(taskEntity: TaskEntity) {
        val localTask = getTaskByIdUseCase.execute(taskEntity.id)

        if (localTask == null) {
            // Si la tarea no existe en la base de datos local, agregarla
            Logger.debug(
                "SyncDataFromDriveUseCase",
                "Agregando nueva tarea desde Google Drive: $taskEntity"
            )
            addTaskUseCase(taskEntity.toDomain())
        } else {
            // Validar si el `updatedAt` de Google Drive es más reciente antes de actualizar
            if (taskEntity.updatedAt.isAfter(localTask.updatedAt)) {
                Logger.debug(
                    "SyncDataFromDriveUseCase",
                    "Actualizando tarea en base de datos local: ${taskEntity.id}"
                )
                updateTaskUseCase(taskEntity.toDomain())
            } else {
                Logger.debug(
                    "SyncDataFromDriveUseCase",
                    "La tarea en Google Drive no es más reciente, no se actualiza."
                )
            }
        }
    }
}