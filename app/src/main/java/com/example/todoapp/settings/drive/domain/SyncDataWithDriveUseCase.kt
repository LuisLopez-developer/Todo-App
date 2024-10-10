package com.example.todoapp.settings.drive.domain

import com.example.todoapp.addtasks.data.toDatabase
import com.example.todoapp.addtasks.domain.GetAllTasksUseCase
import com.example.todoapp.core.NetWorkService
import com.example.todoapp.settings.drive.data.DriveEntity
import com.example.todoapp.settings.drive.data.GoogleDriveRepository
import com.example.todoapp.settings.drive.data.GoogleDriveRepository.EntityType
import com.example.todoapp.settings.utils.toJson
import com.example.todoapp.state.data.constants.DefaultStateId.DELETED_ID
import com.example.todoapp.taskcategory.data.toDatabase
import com.example.todoapp.taskcategory.domain.GetAllCategoriesUseCase
import com.example.todoapp.utils.Logger
import com.google.api.services.drive.Drive
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import org.threeten.bp.OffsetDateTime
import javax.inject.Inject

class SyncDataWithDriveUseCase @Inject constructor(
    private val driveRepository: GoogleDriveRepository,
    private val getAllTasksUseCase: GetAllTasksUseCase,
    private val getAllCategoriesUseCase: GetAllCategoriesUseCase,
    private val netWorkService: NetWorkService,
) {
    suspend operator fun invoke(accessToken: String) = withContext(Dispatchers.IO) {
        if (!netWorkService.getNetworkService()) {
            return@withContext
        }

        val driveService = driveRepository.getDrive(accessToken)

        getAllCategoriesUseCase().first().forEach { category ->
            saveOrDeleteInDrive(
                DriveEntity.Category(category.toDatabase()),
                EntityType.CATEGORY,
                driveService
            )
        }
        getAllTasksUseCase().first().forEach { task ->
            saveOrDeleteInDrive(
                DriveEntity.Task(task.toDatabase()),
                EntityType.TASK,
                driveService
            )
        }
    }

    private fun saveOrDeleteInDrive(
        entity: DriveEntity,
        type: EntityType,
        driveService: Drive,
    ) {
        val (entityId, entityUpdateAt, entityStateId) = when (entity) {
            is DriveEntity.Task -> Triple(
                entity.taskEntity.id,
                entity.taskEntity.updatedAt,
                entity.taskEntity.stateId
            )

            is DriveEntity.Category -> Triple(
                entity.categoryEntity.id,
                entity.categoryEntity.updatedAt,
                entity.categoryEntity.stateId
            )
        }

        val existingFile = driveRepository.searchFileInDrive(driveService, entityId, type.value)

        if (entityStateId == DELETED_ID) {
            if (existingFile != null) {
                driveRepository.deleteFileInDrive(driveService, existingFile.id)
            }
            return
        }

        if (existingFile != null) {
            val existingContent = driveRepository.downloadFileContent(driveService, existingFile.id)
            Logger.debug("SyncDataWithDriveUseCase", "Existing content: $existingContent")
            val newContent = entity.toJson()

            if (existingContent == newContent) {
                return
            }

            val existingUpdateAt =
                existingFile.properties?.get("updatedAt")?.let { OffsetDateTime.parse(it) }

            // Si la fecha de actualización de la entidad en la base de datos local es anterior a la
            // fecha de actualización de la entidad en Google Drive, actualizar la entidad en Google Drive
            if (existingUpdateAt != null && entityUpdateAt.isBefore(existingUpdateAt)) {
                return
            } else {
                driveRepository.updateFileInDrive(driveService, existingFile.id, newContent)
                return
            }
        }

        Logger.debug("SyncDataWithDriveUseCase", "Saving new file in Drive ${entity.toJson()}")
        driveRepository.createFileInDrive(
            driveService,
            entityId,
            entityUpdateAt,
            type,
            entity.toJson()
        )
    }

}