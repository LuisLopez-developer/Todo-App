package com.example.todoapp.settings.drive.data

import android.util.Log
import com.example.todoapp.addtasks.data.TaskDao
import com.example.todoapp.addtasks.data.TaskEntity
import com.example.todoapp.taskcategory.data.CategoryDao
import com.example.todoapp.taskcategory.data.CategoryEntity
import com.google.api.client.http.InputStreamContent
import com.google.api.services.drive.Drive
import com.google.api.services.drive.model.File
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import org.threeten.bp.OffsetDateTime
import javax.inject.Inject

class GoogleDriveRepository @Inject constructor(
    private val categoryDao: CategoryDao,
    private val taskDao: TaskDao,
) {
    private suspend fun saveToDrive(entity: Any, type: EntityType, accessToken: String) =
        withContext(Dispatchers.IO) {
            val (entityId, entityUpdateAt) = when (entity) {
                is TaskEntity -> entity.id to entity.updatedAt
                is CategoryEntity -> entity.id to entity.updatedAt
                else -> throw IllegalArgumentException("Unsupported entity type")
            }

            val driveService = getDriveService(accessToken)
            val existingFile = searchFileInDrive(driveService, entityId, type.value)

            if (existingFile != null) {
                val existingContent = downloadFileContent(driveService, existingFile.id)
                val newContent = entity.toJson()

                if (existingContent == newContent) {
                    logMessage("${type.name} con ID=$entityId ya existe y es idéntico. No se guardará.")
                    return@withContext
                }

                val existingUpdateAt =
                    existingFile.properties?.get("updatedAt")?.let { OffsetDateTime.parse(it) }
                if (existingUpdateAt != null && entityUpdateAt.isBefore(existingUpdateAt)) {
                    logMessage("${type.name} con ID=$entityId es más antigua que la versión en Google Drive. No se actualizará.")
                    return@withContext
                } else {
                    logMessage("${type.name} con ID=$entityId existe pero es diferente. Actualizando...")
                    updateFileInDrive(driveService, existingFile.id, newContent)
                    return@withContext
                }
            }

            createFileInDrive(driveService, entityId, entityUpdateAt, type, entity.toJson())
        }

    private fun searchFileInDrive(driveService: Drive, entityId: String, type: String): File? {
        val query =
            "name='$entityId' and mimeType='application/json' and properties has { key='type' and value='$type' }"
        return executeDriveQuery(driveService, query)?.firstOrNull()
    }

    private fun downloadFileContent(driveService: Drive, fileId: String): String? {
        return executeDriveAction {
            val inputStream = driveService.files().get(fileId).executeMediaAsInputStream()
            inputStream.bufferedReader().use { it.readText() }
        }
    }

    private fun updateFileInDrive(driveService: Drive, fileId: String, newContent: String) {
        val contentStream =
            InputStreamContent("application/json", newContent.toByteArray().inputStream())
        executeDriveAction {
            driveService.files().update(fileId, null, contentStream).setFields("id, name").execute()
            logMessage("Archivo actualizado en Google Drive: ID=$fileId")
        }
    }

    private fun createFileInDrive(
        driveService: Drive,
        entityId: String,
        entityUpdateAt: OffsetDateTime,
        type: EntityType,
        content: String,
    ) {
        val fileMetadata = File().apply {
            name = entityId
            mimeType = "application/json"
            parents = listOf("appDataFolder")
            properties = mapOf("type" to type.value, "updatedAt" to entityUpdateAt.toString())
        }
        val contentStream =
            InputStreamContent("application/json", content.toByteArray().inputStream())
        executeDriveAction {
            val file = driveService.files().create(fileMetadata, contentStream)
                .setFields("id, name, properties").execute()
            logMessage("${type.name} guardada en Google Drive: ID=${file.id}, Nombre=${file.name}")
        }
    }

    // Sincroniza los datos de la aplicación con Google Drive
    suspend fun syncDataWithGoogleDrive(accessToken: String) = withContext(Dispatchers.IO) {
        categoryDao.getCategory().first().forEach { saveToDrive(it, EntityType.CATEGORY, accessToken) }
        taskDao.getTasks().first().forEach { saveToDrive(it, EntityType.TASK, accessToken) }
    }

    // Sincroniza los datos de Google Drive con la aplicación
    suspend fun syncDataFromGoogleDrive(accessToken: String) = withContext(Dispatchers.IO) {
        val driveService = getDriveService(accessToken)
        handleCategories(
            retrieveFilesFromDrive(
                driveService,
                "category",
                CategoryEntity::class.java
            )
        )
        handleTasks(retrieveFilesFromDrive(driveService, "task", TaskEntity::class.java))
    }

    // Recupera archivos de Google Drive y los convierte a una lista del tipo especificado
    private fun <T> retrieveFilesFromDrive(
        driveService: Drive,
        type: String,
        clazz: Class<T>,
    ): List<T> {
        val query =
            "mimeType='application/json' and properties has { key='type' and value='$type' }"
        return executeDriveQuery(driveService, query)?.mapNotNull { file ->
            executeDriveAction {
                val inputStream = driveService.files().get(file.id).executeMediaAsInputStream()
                inputStream.bufferedReader()
                    .use { reader -> Gson().fromJson(reader.readText(), clazz) }
            }
        } ?: emptyList()
    }

    // Elimina todos los archivos de la aplicación en Google Drive
    suspend fun clearAppDataFromGoogleDrive(accessToken: String) = withContext(Dispatchers.IO) {
        val driveService = getDriveService(accessToken)
        val files =
            executeDriveQuery(driveService, "mimeType='application/json'") ?: return@withContext
        logMessage("Archivos encontrados para eliminar: ${files.size}")
        files.forEach { executeDriveAction { driveService.files().delete(it.id).execute() } }
    }

    // Maneja las tareas recuperadas de Google Drive
    private suspend fun handleTasks(tasks: List<TaskEntity>) {
        tasks.forEach { taskEntity ->
            try {
                val existingTask = taskDao.getTaskById(taskEntity.id)
                if (existingTask == null) {
                    taskDao.addTask(taskEntity)
                    logMessage("Tarea añadida: ${taskEntity.task}")
                } else if (taskEntity.updatedAt.isAfter(existingTask.updatedAt)) {
                    taskDao.updateTask(taskEntity)
                    logMessage("Tarea actualizada: ${taskEntity.task}")
                } else {
                    logMessage("La tarea ya existe y está actualizada: ${taskEntity.task}")
                }
            } catch (e: Exception) {
                logError("Error al manejar tarea: ${taskEntity.task}", e)
            }
        }
    }

    // Maneja las categorías recuperadas de Google Drive
    private suspend fun handleCategories(categories: List<CategoryEntity>) {
        categories.forEach { categoryEntity ->
            try {
                if (categoryEntity.userId != null && !categoryDao.isUserIdValid(categoryEntity.userId)) {
                    logError("Invalid userId: ${categoryEntity.userId}")
                    return@forEach
                }
                val existingCategory = categoryDao.getCategoryById(categoryEntity.id)
                if (existingCategory == null) {
                    categoryDao.addCategory(categoryEntity)
                    logMessage("Categoria añadida: ${categoryEntity.category}")
                } else if (categoryEntity.updatedAt.isAfter(existingCategory.updatedAt)) {
                    categoryDao.updateCategory(categoryEntity)
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
        Log.d("GoogleDriveRepository", message)
    }

    private fun logError(message: String, e: Exception? = null) {
        Log.e("GoogleDriveRepository", message, e)
    }

    // Ejecuta una acción en Google Drive
    private fun <T> executeDriveAction(action: () -> T): T? {
        return try {
            action()
        } catch (e: Exception) {
            logError("Error en la acción de Google Drive", e)
            null
        }
    }

    // Ejecuta una consulta en Google Drive
    private fun executeDriveQuery(driveService: Drive, query: String): List<File>? {
        return executeDriveAction {
            driveService.files().list()
                .setSpaces("appDataFolder")
                .setQ(query)
                .setFields("files(id, name)")
                .execute()
                .files
        }
    }

    private enum class EntityType(val value: String) {
        TASK("task"),
        CATEGORY("category")
    }
}

private fun Any.toJson(): String {
    return Gson().toJson(this)
}