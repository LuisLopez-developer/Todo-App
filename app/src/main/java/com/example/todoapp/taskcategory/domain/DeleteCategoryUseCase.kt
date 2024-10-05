package com.example.todoapp.taskcategory.domain


import com.example.todoapp.addtasks.domain.DeleteTasksByCategoryLogicallyUseCase
import com.example.todoapp.taskcategory.data.CategoryRepository
import com.example.todoapp.taskcategory.ui.model.TaskCategoryModel
import javax.inject.Inject

class DeleteCategoryUseCase @Inject constructor(
    private val repository: CategoryRepository,
    private val deleteTasksByCategoryLogicallyUseCase: DeleteTasksByCategoryLogicallyUseCase,
) {
    suspend operator fun invoke(taskCategoryModel: TaskCategoryModel) {
        // Se crea un nuevo nombre de categoría con la fecha actual, para evitar duplicados
        val newCategoryName = "${taskCategoryModel.category}_${System.currentTimeMillis()}"
        repository.deleteCategoryLogically(taskCategoryModel.id, newCategoryName)

        deleteTasksByCategoryLogicallyUseCase(taskCategoryModel.id)
    }
}