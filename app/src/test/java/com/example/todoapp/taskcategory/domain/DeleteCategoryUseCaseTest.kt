package com.example.todoapp.taskcategory.domain

import com.example.todoapp.addtasks.domain.DeleteTasksByCategoryLogicallyUseCase
import com.example.todoapp.taskcategory.data.CategoryRepository
import com.example.todoapp.taskcategory.ui.model.TaskCategoryModel
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerifyOrder
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test


class DeleteCategoryUseCaseTest {

    @MockK
    private lateinit var repository: CategoryRepository

    @MockK
    private lateinit var deleteTasksByCategoryLogicallyUseCase: DeleteTasksByCategoryLogicallyUseCase

    private lateinit var deleteCategoryUseCase: DeleteCategoryUseCase

    @Before
    fun onBefore() {
        MockKAnnotations.init(this)

        // Inicializamos el caso de uso
        deleteCategoryUseCase =
            DeleteCategoryUseCase(repository, deleteTasksByCategoryLogicallyUseCase)

        // Mock de la función invoke para evitar llamadas reales
        coEvery { deleteTasksByCategoryLogicallyUseCase.invoke(any()) } returns Unit
    }

    @Test
    fun `category name is updated to avoid duplicates when deleted`() = runBlocking {
        // Given
        val taskCategoryModel = TaskCategoryModel(id = "1", category = "Work")
        var capturedNewCategoryName: String? = null

        // Mock del método de eliminación lógica de la categoría para capturar el nuevo nombre generado
        coEvery { repository.deleteCategoryLogically(taskCategoryModel.id, any()) } answers {
            capturedNewCategoryName = secondArg() // Captura el nuevo nombre de la categoría
        }

        // When
        deleteCategoryUseCase(taskCategoryModel)

        // Then
        // Verificar que el nombre de la categoría ha cambiado
        assert(capturedNewCategoryName != null) { "New category name should not be null" }
        assert(capturedNewCategoryName != taskCategoryModel.category) { "Category name should be updated to avoid duplicates" }
    }

    @Test
    fun `delete category is called before deleting associated tasks`() = runBlocking {
        // Given
        val taskCategoryModel = TaskCategoryModel(id = "1", category = "Work")

        // Mock del método de eliminación lógica de la categoría
        coEvery { repository.deleteCategoryLogically(taskCategoryModel.id, any()) } returns Unit

        // Mock del caso de uso para eliminar las tareas asociadas
        coEvery { deleteTasksByCategoryLogicallyUseCase.invoke(taskCategoryModel.id) } returns Unit

        // When
        deleteCategoryUseCase(taskCategoryModel)

        // Then
        // Verificar el orden de las llamadas: primero la categoría, luego las tareas
        coVerifyOrder {
            repository.deleteCategoryLogically(
                taskCategoryModel.id,
                any()
            ) // Cambia el nombre primero
            deleteTasksByCategoryLogicallyUseCase.invoke(taskCategoryModel.id) // Luego elimina las tareas
        }
    }

}