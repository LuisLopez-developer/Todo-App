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
    private val taskDao: TaskDao
) {
    // Configura el servicio de Google Drive utilizando el token de acceso proporcionado
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

    // Guarda una entidad (tarea o categoría) en Google Drive
    private suspend fun saveToDrive(entity: Any, type: EntityType, accessToken: String) = withContext(Dispatchers.IO) {
        // Configuración de metadatos del archivo para Google Drive
        val entityId = when (entity) {
            is TaskEntity -> entity.id
            is CategoryEntity -> entity.id
            else -> throw IllegalArgumentException("Tipo de entidad no compatible")
        }

        val driveService = getDriveService(accessToken)

        // Verifica si ya existe un archivo con este ID en Google Drive
        val existingFile = searchFileInDrive(driveService, entityId, type.value)

        if (existingFile != null) {
            // Si el archivo existe, compara su contenido con el de la entidad
            val existingContent = downloadFileContent(driveService, existingFile.id)
            val newContent = entity.toJson()

            if (existingContent == newContent) {
                Log.d("GoogleDriveRepository", "${type.name} con ID=$entityId ya existe y es idéntico. No se guardará. $newContent")
                return@withContext // No hacer nada si el contenido es el mismo
            } else {
                Log.d("GoogleDriveRepository", "${type.name} con ID=$entityId existe pero es diferente. Actualizando...")
                updateFileInDrive(driveService, existingFile.id, newContent)
                return@withContext
            }
        }

        // Si el archivo no existe, crea uno nuevo
        val fileMetadata = File().apply {
            name = entityId
            mimeType = "application/json"
            parents = listOf("appDataFolder") // Guardar en la carpeta de datos de la aplicación
            properties = mapOf("type" to type.value)
        }

        val contentStream = InputStreamContent("application/json", entity.toJson().toByteArray().inputStream())

        try {
            // Crea el archivo en Google Drive
            val file = driveService.files().create(fileMetadata, contentStream)
                .setFields("id, name, properties")
                .execute()
            Log.d("GoogleDriveRepository", "${type.name} guardada en Google Drive: ID=${file.id}, Nombre=${file.name}")
        } catch (e: Exception) {
            Log.e("GoogleDriveRepository", "Error al guardar ${type.name}: ${e.message}", e)
        }
    }

    // Busca un archivo en Google Drive por su 'entityId' y tipo (tarea o categoría).
    private fun searchFileInDrive(driveService: Drive, entityId: String, type: String): File? {
        val mimeType = "application/json"
        val query = "name='$entityId' and mimeType='$mimeType' and properties has { key='type' and value='$type' }"

        return try {
            val files = driveService.files().list()
                .setSpaces("appDataFolder")
                .setQ(query)
                .setFields("files(id, name)")
                .execute()
                .files

            if (files.isNotEmpty()) files.first() else null
        } catch (e: Exception) {
            Log.e("GoogleDriveRepository", "Error al buscar archivo en Google Drive: $e")
            null
        }
    }

    // Descarga el contenido de un archivo de Google Drive usando su 'fileId' y lo devuelve como un String.
    private fun downloadFileContent(driveService: Drive, fileId: String): String? {
        return try {
            val inputStream = driveService.files().get(fileId).executeMediaAsInputStream()
            inputStream.bufferedReader().use { it.readText() }
        } catch (e: Exception) {
            Log.e("GoogleDriveRepository", "Error al descargar contenido del archivo: $fileId", e)
            null
        }
    }

    // Actualiza un archivo existente en Google Drive con nuevo contenido (newContent) utilizando su 'fileId'.
    private fun updateFileInDrive(driveService: Drive, fileId: String, newContent: String) {
        val contentStream = InputStreamContent("application/json", newContent.toByteArray().inputStream())

        try {
            val file = driveService.files().update(fileId, null, contentStream)
                .setFields("id, name")
                .execute()
            Log.d("GoogleDriveRepository", "Archivo actualizado en Google Drive: ID=${file.id}")
        } catch (e: Exception) {
            Log.e("GoogleDriveRepository", "Error al actualizar archivo en Google Drive: ID=$fileId", e)
        }
    }

    // Sincroniza datos locales con Google Drive
    suspend fun syncDataWithGoogleDrive(accessToken: String) = withContext(Dispatchers.IO) {
        // Guarda todas las categorías en Google Drive
        categoryDao.getCategory().first().forEach { categoryEntity ->
            saveToDrive(categoryEntity, EntityType.CATEGORY, accessToken)
        }
        // Guarda todas las tareas en Google Drive
        taskDao.getTasks().first().forEach { taskEntity ->
            saveToDrive(taskEntity, EntityType.TASK, accessToken)
        }
    }

    // Sincroniza datos desde Google Drive a la base de datos local
    suspend fun syncDataFromGoogleDrive(accessToken: String) = withContext(Dispatchers.IO) {
        val driveService = getDriveService(accessToken)

        // Recupera y maneja las categorías desde Google Drive
        val categories = retrieveFilesFromDrive(driveService, "category", CategoryEntity::class.java)
        handleCategories(categories)

        // Recupera y maneja las tareas desde Google Drive
        val tasks = retrieveFilesFromDrive(driveService, "task", TaskEntity::class.java)
        handleTasks(tasks)
    }

    // Recupera archivos de Google Drive y los convierte a una lista del tipo especificado
    private fun <T> retrieveFilesFromDrive(driveService: Drive, type: String, clazz: Class<T>): List<T> {
        val mimeType = "application/json"
        // Construye la consulta para filtrar archivos según el tipo
        val query = if (type == "category") {
            "mimeType='$mimeType' and properties has { key='type' and value='category' }"
        } else {
            "mimeType='$mimeType' and not properties has { key='type' and value='category' }"
        }

        // Lista de archivos en Google Drive que coinciden con la consulta
        val files = driveService.files().list()
            .setSpaces("appDataFolder")
            .setQ(query)
            .setFields("files(id, name)")
            .execute()
            .files

        // Mapea cada archivo recuperado a una instancia del tipo especificado
        return files.mapNotNull { file ->
            try {
                val inputStream = driveService.files().get(file.id).executeMediaAsInputStream()
                inputStream.bufferedReader().use { reader ->
                    val json = reader.readText()
                    Gson().fromJson(json, clazz) // Usa Gson para convertir directamente a T
                }
            } catch (e: Exception) {
                Log.e("GoogleDriveRepository", "Error al recuperar archivo: ${file.name}", e)
                null
            }
        }
    }

    // Elimina todos los archivos de la carpeta de datos de la aplicación en Google Drive
    suspend fun clearAppDataFromGoogleDrive(accessToken: String) = withContext(Dispatchers.IO) {
        val driveService = getDriveService(accessToken)
        // Recupera todos los archivos en la carpeta de datos de la aplicación
        val files = driveService.files().list()
            .setSpaces("appDataFolder")
            .setFields("files(id, name)")
            .execute()
            .files

        Log.d("GoogleDriveRepository", "Archivos encontrados para eliminar: ${files.size}")
        // Elimina cada archivo encontrado
        files.forEach { file ->
            try {
                driveService.files().delete(file.id).execute()
                Log.d("GoogleDriveRepository", "Archivo eliminado: ID=${file.id}, Nombre=${file.name}")
            } catch (e: Exception) {
                Log.e("GoogleDriveRepository", "Error al eliminar archivo: ID=${file.id}, Nombre=${file.name}", e)
            }
        }
    }

    // Maneja la adición de categorías recuperadas de Google Drive a la base de datos local
    private suspend fun handleCategories(categories: List<CategoryEntity>) {
        categories.forEach { categoryEntity ->
            try {
                if (categoryEntity.userId != null && !categoryDao.isUserIdValid(categoryEntity.userId)) {
                    Log.e("GoogleDriveRepository", "Invalid userId: ${categoryEntity.userId}")
                    return@forEach
                }

                // Si la categoría no existe, se añade a la base de datos
                if (categoryDao.getCategoryById(categoryEntity.id) == null) {
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

    // Maneja la adición de tareas recuperadas de Google Drive a la base de datos local
    private suspend fun handleTasks(tasks: List<TaskEntity>) {
        tasks.forEach { taskEntity ->
            try {
                val existingTask = taskDao.getTaskById(taskEntity.id)
                // Si la tarea no existe o es diferente de la tarea, se añade a la base de datos
                if (existingTask == null || existingTask.id != taskEntity.id) {
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

    // Enum que define los tipos de entidades que se pueden manejar (Tarea, Categoría)
    private enum class EntityType(val value: String) {
        TASK("task"),
        CATEGORY("category")
    }
}

// Funciones de extensión para convertir entidades a JSON
private fun Any.toJson(): String {
    return Gson().toJson(this)
}