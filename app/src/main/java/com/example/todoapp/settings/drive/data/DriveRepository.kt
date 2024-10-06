package com.example.todoapp.settings.drive.data

import android.util.Log
import com.google.api.client.http.InputStreamContent
import com.google.api.services.drive.Drive
import com.google.api.services.drive.model.File
import com.google.gson.Gson
import org.threeten.bp.OffsetDateTime
import javax.inject.Inject

class GoogleDriveRepository @Inject constructor() {
    fun getDrive(accessToken: String): Drive = getDriveService(accessToken)

    fun searchFileInDrive(driveService: Drive, entityId: String, type: String): File? {
        val query =
            "name='$entityId' and mimeType='application/json' and properties has { key='type' and value='$type' }"
        return executeDriveQuery(driveService, query)?.firstOrNull()
    }

    fun downloadFileContent(driveService: Drive, fileId: String): String? {
        return executeDriveAction {
            val inputStream = driveService.files().get(fileId).executeMediaAsInputStream()
            inputStream.bufferedReader().use { it.readText() }
        }
    }

    fun updateFileInDrive(driveService: Drive, fileId: String, newContent: String) {
        val contentStream =
            InputStreamContent("application/json", newContent.toByteArray().inputStream())
        executeDriveAction {
            driveService.files().update(fileId, null, contentStream).setFields("id, name").execute()
            logMessage("Archivo actualizado en Google Drive: ID=$fileId")
        }
    }

    fun createFileInDrive(
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

    fun deleteFileInDrive(driveService: Drive, fileId: String) {
        executeDriveAction {
            driveService.files().delete(fileId).execute()
            logMessage("Archivo eliminado en Google Drive: ID=$fileId")
        }
    }

    fun <T> retrieveFilesFromDrive(
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

    fun clearAppDataFromGoogleDrive(driveService: Drive) {
        val files =
            executeDriveQuery(driveService, "mimeType='application/json'") ?: return
        logMessage("Archivos encontrados para eliminar: ${files.size}")
        files.forEach { executeDriveAction { driveService.files().delete(it.id).execute() } }
    }

    private fun logMessage(message: String) {
        Log.d("GoogleDriveRepository", message)
    }

    private fun logError(message: String, e: Exception? = null) {
        Log.e("GoogleDriveRepository", "$message ${e?.message}")
    }

    private fun <T> executeDriveAction(action: () -> T): T? {
        return try {
            action()
        } catch (e: Exception) {
            logError("Error en la acción de Google Drive", e)
            null
        }
    }

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

    enum class EntityType(val value: String) {
        TASK("task"),
        CATEGORY("category")
    }
}