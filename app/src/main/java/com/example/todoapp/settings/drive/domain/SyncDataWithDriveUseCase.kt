package com.example.todoapp.settings.drive.domain

import com.example.todoapp.settings.drive.data.GoogleDriveRepository
import javax.inject.Inject

class SyncDataWithDriveUseCase @Inject constructor(private val driveRepository: GoogleDriveRepository) {
    suspend operator fun invoke(accessToken: String) {
        driveRepository.syncDataWithGoogleDrive(accessToken)
    }
}