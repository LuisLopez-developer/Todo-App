package com.example.todoapp.settings.drive.domain

import com.example.todoapp.settings.drive.data.GoogleDriveRepository
import javax.inject.Inject

class ClearAppDataFromGoogleDriveUseCase @Inject constructor(private val driveRepository: GoogleDriveRepository) {
    suspend operator fun invoke(accessToken: String) {
        driveRepository.clearAppDataFromGoogleDrive(accessToken)
    }
}