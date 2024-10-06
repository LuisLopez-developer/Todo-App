package com.example.todoapp.settings.drive.domain

import android.util.Log
import com.example.todoapp.addtasks.data.TaskEntity
import com.example.todoapp.addtasks.domain.GetTaskUseCase
import com.example.todoapp.settings.drive.data.GoogleDriveRepository
import com.example.todoapp.settings.utils.toJson
import com.example.todoapp.state.data.constants.DefaultStateId.DELETED_ID
import com.example.todoapp.taskcategory.data.CategoryEntity
import com.example.todoapp.taskcategory.domain.GetCategoryUseCase
import com.google.api.services.drive.Drive
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.threeten.bp.OffsetDateTime
import javax.inject.Inject

class SyncDataWithDriveUseCase @Inject constructor(
    private val driveRepository: GoogleDriveRepository,
    private val getTaskUseCase: GetTaskUseCase,
    private val getCategoryUseCase: GetCategoryUseCase,
) {
    suspend operator fun invoke(accessToken: String) = withContext(Dispatchers.IO) {
        val driveService = driveRepository.getDrive(accessToken)
        getCategoryUseCase().collect { category ->
            saveOrDeleteInDrive(category, GoogleDriveRepository.EntityType.CATEGORY, driveService)
        }
        getTaskUseCase().collect { task ->
            saveOrDeleteInDrive(task, GoogleDriveRepository.EntityType.TASK, driveService)
        }
    }

    private fun saveOrDeleteInDrive(
        entity: Any,
        type: GoogleDriveRepository.EntityType,
        driveService: Drive,
    ) {
        val (entityId, entityUpdateAt, entityStateId) = when (entity) {
            is TaskEntity -> Triple(entity.id, entity.updatedAt, entity.stateId)
            is CategoryEntity -> Triple(entity.id, entity.updatedAt, entity.stateId)
            else -> throw IllegalArgumentException("Unsupported entity type")
        }

        val existingFile = driveRepository.searchFileInDrive(driveService, entityId, type.value)

        if (entityStateId == DELETED_ID) {
            if (existingFile != null) {
                driveRepository.deleteFileInDrive(driveService, existingFile.id)
                logMessage("${type.name} con ID=$entityId eliminada de Google Drive.")
            }
            return
        }

        if (existingFile != null) {
            val existingContent = driveRepository.downloadFileContent(driveService, existingFile.id)
            val newContent = entity.toJson()

            if (existingContent == newContent) {
                logMessage("${type.name} con ID=$entityId ya existe y es idéntico. No se guardará.")
                return
            }

            val existingUpdateAt =
                existingFile.properties?.get("updatedAt")?.let { OffsetDateTime.parse(it) }
            if (existingUpdateAt != null && entityUpdateAt.isBefore(existingUpdateAt)) {
                logMessage("${type.name} con ID=$entityId es más antigua que la versión en Google Drive. No se actualizará.")
                return
            } else {
                logMessage("${type.name} con ID=$entityId existe pero es diferente. Actualizando...")
                driveRepository.updateFileInDrive(driveService, existingFile.id, newContent)
                return
            }
        }

        driveRepository.createFileInDrive(
            driveService,
            entityId,
            entityUpdateAt,
            type,
            entity.toJson()
        )
    }

    private fun logMessage(message: String) {
        Log.d("SyncDataWithDriveUseCase", message)
    }
}