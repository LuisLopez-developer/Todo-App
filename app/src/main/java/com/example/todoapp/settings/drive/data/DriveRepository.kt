package com.example.todoapp.settings.drive.data

import android.util.Log
import com.example.todoapp.addtasks.data.TaskDao
import com.example.todoapp.addtasks.data.TaskEntity
import com.example.todoapp.taskcategory.data.CategoryDao
import com.example.todoapp.taskcategory.data.CategoryEntity
import com.google.api.client.http.InputStreamContent
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.gson.GsonFactory
import com.google.api.services.drive.Drive
import com.google.api.services.drive.model.File
import com.google.auth.http.HttpCredentialsAdapter
import com.google.auth.oauth2.AccessToken
import com.google.auth.oauth2.GoogleCredentials
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import javax.inject.Inject

class GoogleDriveRepository @Inject constructor(
    private val categoryDao: CategoryDao,
    private val taskDao: TaskDao,

) {

    private fun getDriveService(accessToken: String): Drive {
        val credentials = GoogleCredentials.create(AccessToken(accessToken, null))
        return Drive.Builder(
            NetHttpTransport(),
            GsonFactory(),
            HttpCredentialsAdapter(credentials)
        )
            .setApplicationName("TodoApp")
            .build()
    }

    private suspend fun saveTaskToDrive(taskEntity: TaskEntity, accessToken: String) = withContext(Dispatchers.IO) {
        val fileMetadata = File().apply {
            name = taskEntity.task
            mimeType = "application/json"
            parents = listOf("appDataFolder")
            properties = mapOf("type" to "task")
        }

        val content = taskEntity.toJson().toByteArray()
        Log.d("GoogleDriveRepository", "Contenido de la tarea: ${taskEntity.toJson()}")
        val contentStream = InputStreamContent("application/json", content.inputStream())

        val file = getDriveService(accessToken).files().create(fileMetadata, contentStream)
            .setFields("id, name, properties")
            .execute()

        Log.d("GoogleDriveRepository", "Tarea guardada en Google Drive: ID=${file.id}, Nombre=${file.name}")
    }

    private suspend fun saveCategoryToDrive(categoryEntity: CategoryEntity, accessToken: String) = withContext(Dispatchers.IO) {
        val fileMetadata = File().apply {
            name = categoryEntity.category
            mimeType = "application/json"
            parents = listOf("appDataFolder")
            properties = mapOf("type" to "category")
        }

        val content = categoryEntity.toJson().toByteArray()
        Log.d("GoogleDriveRepository", "Contenido de la categoría: ${categoryEntity.toJson()}")
        val contentStream = InputStreamContent("application/json", content.inputStream())

        val file = getDriveService(accessToken).files().create(fileMetadata, contentStream)
            .setFields("id, name, properties")
            .execute()

        Log.d("GoogleDriveRepository", "Categoría guardada en Google Drive: ID=${file.id}, Nombre=${file.name}")
    }

    // Sincronizar datos con Google Drive (guardando datos locales en Google Drive)
    suspend fun syncDataWithGoogleDrive(accessToken: String) = withContext(Dispatchers.IO) {
        categoryDao.getCategory().first().forEach { categoryEntity ->
            saveCategoryToDrive(categoryEntity, accessToken)
        }

        taskDao.getTasks().first().forEach { taskEntity ->
            saveTaskToDrive(taskEntity, accessToken)
        }
    }

    // Sincronizar datos desde Google Drive (obteniendo datos de Google Drive a la base de datos local)
    suspend fun syncDataFromGoogleDrive(accessToken: String) = withContext(Dispatchers.IO) {
        val driveService = getDriveService(accessToken)

        // Recuperar archivos de categorías desde Google Drive
        val categoryFiles = driveService.files().list()
            .setSpaces("appDataFolder")
            .setQ("mimeType='application/json' and properties has { key='type' and value='category' }")
            .setFields("files(id, name, properties)")
            .execute()
            .files

        Log.d("GoogleDriveRepository", "Categorías encontradas: ${categoryFiles.size}")

        categoryFiles.forEach { file ->
            Log.d("GoogleDriveRepository", "Archivo encontrado: Nombre=${file.name}, ID=${file.id}")
        }

        val categories = categoryFiles.mapNotNull { file ->
            val inputStream = driveService.files().get(file.id).executeMediaAsInputStream()
            val categoryEntity = inputStream.bufferedReader().use { it.readText() }.fromJson<CategoryEntity>()
            categoryEntity
        }

        handleCategories(categories)

        // Recuperar archivos de tareas desde Google Drive
        val taskFiles = driveService.files().list()
            .setSpaces("appDataFolder")
            .setQ("mimeType='application/json' and not properties has { key='type' and value='category' }")
            .setFields("files(id, name)")
            .execute()
            .files

        Log.d("GoogleDriveRepository", "Tareas encontradas: ${taskFiles.size}")

        taskFiles.forEach { file ->
            Log.d("GoogleDriveRepository", "Archivo encontrado: Nombre=${file.name}, ID=${file.id}")
        }

        val tasks = taskFiles.mapNotNull { file ->
            val inputStream = driveService.files().get(file.id).executeMediaAsInputStream()
            val taskEntity = inputStream.bufferedReader().use { it.readText() }.fromJson<TaskEntity>()
            taskEntity
        }

        handleTasks(tasks)

    }

    suspend fun clearAppDataFromGoogleDrive(accessToken: String) = withContext(Dispatchers.IO) {
        val driveService = getDriveService(accessToken)

        // List all files in the appDataFolder
        val files = driveService.files().list()
            .setSpaces("appDataFolder")
            .setFields("files(id, name)")
            .execute()
            .files

        Log.d("GoogleDriveRepository", "Archivos encontrados para eliminar: ${files.size}")

        // Delete each file
        files.forEach { file ->
            try {
                driveService.files().delete(file.id).execute()
                Log.d("GoogleDriveRepository", "Archivo eliminado: ID=${file.id}, Nombre=${file.name}")
            } catch (e: Exception) {
                Log.e("GoogleDriveRepository", "Error al eliminar archivo: ID=${file.id}, Nombre=${file.name}", e)
            }
        }
    }


    // Manejar categorías obtenidas de Google Drive
    private suspend fun handleCategories(categories: List<CategoryEntity>) {
        categories.forEach { categoryEntity ->
            try {
                Log.d("GoogleDriveRepository", "Manejando categoría: $categoryEntity")
                // Validate userId only if it is not null
                if (categoryEntity.userId != null && !categoryDao.isUserIdValid(categoryEntity.userId)) {
                    Log.e("GoogleDriveRepository", "Invalid userId: ${categoryEntity.userId}")
                    return@forEach
                }

                val existingCategory = categoryDao.getCategoryById(categoryEntity.id)
                if (existingCategory == null) {
                    categoryDao.addCategory(categoryEntity)
                    Log.d("GoogleDriveRepository", "Categoria añadida: ${categoryEntity.category}")
                } else {
                    Log.d("GoogleDriveRepository", "La categoría ya existe: ${categoryEntity.category}")
                }
            } catch (e: Exception) {
                Log.e("GoogleDriveRepository", "Error al agregar categoría: ${categoryEntity.category}", e)
            }
        }
    }

    // Manejar tareas obtenidas de Google Drive
    private suspend fun handleTasks(tasks: List<TaskEntity>) {
        tasks.forEach { taskEntity ->
            try {
                val existingTask = taskDao.getTaskById(taskEntity.id)
                if (existingTask == null || existingTask.task != taskEntity.task) {
                    taskDao.addTask(taskEntity)
                    Log.d("GoogleDriveRepository", "Tarea añadida: ${taskEntity.task}")
                } else {
                    Log.d("GoogleDriveRepository", "La tarea ya existe: ${taskEntity.task}")
                }
            } catch (e: Exception) {
                Log.e("GoogleDriveRepository", "Error al agregar tarea: ${taskEntity.task}", e)
            }
        }
    }
}

inline fun <reified T> String.fromJson(): T {
    return Gson().fromJson(this, T::class.java)
}

fun TaskEntity.toJson(): String {
    return Gson().toJson(this)
}

fun CategoryEntity.toJson(): String {
    return Gson().toJson(this)
}