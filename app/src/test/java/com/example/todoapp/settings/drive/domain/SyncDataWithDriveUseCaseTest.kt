package com.example.todoapp.settings.drive.domain

import com.example.todoapp.addtasks.data.TaskEntity
import com.example.todoapp.addtasks.domain.GetAllTasksUseCase
import com.example.todoapp.addtasks.domain.model.toDomain
import com.example.todoapp.core.NetWorkService
import com.example.todoapp.settings.drive.data.GoogleDriveRepository
import com.example.todoapp.settings.drive.data.GoogleDriveRepository.EntityType
import com.example.todoapp.state.data.constants.DefaultStateId.DELETED_ID
import com.example.todoapp.taskcategory.data.CategoryEntity
import com.example.todoapp.taskcategory.domain.GetAllCategoriesUseCase
import com.example.todoapp.taskcategory.domain.model.toDomain
import com.google.api.services.drive.Drive
import com.google.api.services.drive.model.File
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerifyOrder
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import org.threeten.bp.OffsetDateTime

class SyncDataWithDriveUseCaseTest {

    @MockK
    private lateinit var driveRepository: GoogleDriveRepository

    @MockK
    private lateinit var getAllTasksUseCase: GetAllTasksUseCase

    @MockK
    private lateinit var getAllCategoriesUseCase: GetAllCategoriesUseCase

    @MockK
    private lateinit var driveService: Drive

    @MockK
    private lateinit var netWorkService: NetWorkService

    private lateinit var syncDataWithDriveUseCase: SyncDataWithDriveUseCase

    @Before
    fun setUp() {
        MockKAnnotations.init(this)

        syncDataWithDriveUseCase = SyncDataWithDriveUseCase(
            driveRepository,
            getAllTasksUseCase,
            getAllCategoriesUseCase,
            netWorkService
        )
    }

    @Test
    fun `test synchronization of categories and tasks`() = runBlocking {
        // Mock network service
        coEvery { netWorkService.getNetworkService() } returns true

        // Mock data
        val categories = listOf(mockCategory().toDomain())
        val tasks = listOf(mockTask().toDomain())

        // Mock use cases
        coEvery { getAllCategoriesUseCase() } returns flowOf(categories)
        coEvery { getAllTasksUseCase() } returns flowOf(tasks)

        // Mock drive service
        coEvery { driveRepository.getDrive(any()) } returns driveService

        // Mock searchFileInDrive responses
        coEvery {
            driveRepository.searchFileInDrive(
                driveService,
                any(),
                EntityType.CATEGORY.value
            )
        } returns null
        coEvery {
            driveRepository.searchFileInDrive(
                driveService,
                any(),
                EntityType.TASK.value
            )
        } returns null

        // Mock createFileInDrive response
        coEvery {
            driveRepository.createFileInDrive(
                any(),
                any(),
                any(),
                any(),
                any()
            )
        } returns mockk()

        // Execute use case
        syncDataWithDriveUseCase("accessToken")

        // Verify interactions
        coVerifyOrder {
            driveRepository.createFileInDrive(
                driveService,
                categories[0].id,
                categories[0].updatedAt,
                EntityType.CATEGORY,
                any()
            )
            driveRepository.createFileInDrive(
                driveService,
                tasks[0].id,
                tasks[0].updatedAt,
                EntityType.TASK,
                any()
            )
        }
    }

    @Test
    fun `test handling of deleted entities`() = runBlocking {
        // Mock network service
        coEvery { netWorkService.getNetworkService() } returns true

        // Mock data
        val deletedCategory = mockCategory(stateId = DELETED_ID).toDomain()
        val deletedTask = mockTask(stateId = DELETED_ID).toDomain()

        // Mock use cases
        coEvery { getAllCategoriesUseCase() } returns flowOf(listOf(deletedCategory))
        coEvery { getAllTasksUseCase() } returns flowOf(listOf(deletedTask))

        // Mock drive service
        coEvery { driveRepository.getDrive(any()) } returns driveService
        val mockFile = mockk<File>()
        coEvery { mockFile.id } returns "fileId"
        coEvery {
            driveRepository.searchFileInDrive(
                driveService,
                deletedCategory.id,
                EntityType.CATEGORY.value
            )
        } returns mockFile
        coEvery {
            driveRepository.searchFileInDrive(
                driveService,
                deletedTask.id,
                EntityType.TASK.value
            )
        } returns mockFile

        // Mock deleteFileInDrive response
        coEvery { driveRepository.deleteFileInDrive(any(), any()) } returns Unit

        // Execute use case
        syncDataWithDriveUseCase("accessToken")

        // Verify interactions
        coVerifyOrder {
            driveRepository.deleteFileInDrive(driveService, "fileId")
        }
    }

    private fun mockCategory(stateId: String = "active"): CategoryEntity {
        return CategoryEntity(
            id = "categoryId",
            category = "category",
            userId = "userId",
            stateId = stateId,
            createdAt = OffsetDateTime.now(),
            updatedAt = OffsetDateTime.now()
        )
    }

    private fun mockTask(stateId: String = "active"): TaskEntity {
        return TaskEntity(
            id = "taskId",
            task = "task",
            details = "description",
            userId = "userId",
            stateId = stateId,
            createdAt = OffsetDateTime.now(),
            updatedAt = OffsetDateTime.now()
        )
    }
}