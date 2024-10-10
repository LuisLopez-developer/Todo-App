package com.example.todoapp.settings.drive.domain

import android.content.Context
import android.widget.Toast
import com.example.todoapp.R.string
import com.example.todoapp.core.NetWorkService
import com.example.todoapp.settings.drive.data.GoogleDriveRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ClearAppDataFromGoogleDriveUseCase @Inject constructor(
    private val driveRepository: GoogleDriveRepository,
    private val netWorkService: NetWorkService,
    @ApplicationContext private val context: Context,
) {
    suspend operator fun invoke(accessToken: String) = withContext(Dispatchers.IO) {
        if (!netWorkService.getNetworkService()) {
            return@withContext
        }
        val driveService = driveRepository.getDrive(accessToken)
        driveRepository.clearAppDataFromGoogleDrive(driveService)

        val totalFiles = driveRepository.countFilesInDrive(driveService)
        val message = if (totalFiles > 0) {
            context.getString(string.total_files) + ": $totalFiles, " + context.getString(string.deleted_error)
        } else {
            context.getString(string.total_files) + ": $totalFiles, " + context.getString(string.deleted_success)
        }

        withContext(Dispatchers.Main) {
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }
    }
}