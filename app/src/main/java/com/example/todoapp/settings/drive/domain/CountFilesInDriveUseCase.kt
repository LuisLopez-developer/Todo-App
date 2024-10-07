package com.example.todoapp.settings.drive.domain

import com.example.todoapp.settings.drive.data.GoogleDriveRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class CountFilesInDriveUseCase @Inject constructor(private val driveRepository: GoogleDriveRepository) {
    suspend operator fun invoke(accessToken: String) = withContext(Dispatchers.IO) {
        driveRepository.countFilesInDrive(driveRepository.getDrive(accessToken))
    }
}