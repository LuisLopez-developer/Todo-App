package com.example.todoapp.taskcategory.domain


import com.example.todoapp.taskcategory.data.CategoryRepository
import com.example.todoapp.taskcategory.ui.model.TaskCategoryModel
import javax.inject.Inject

class DeleteCategoryUseCase @Inject constructor(private val categoryRepository: CategoryRepository) {
    suspend operator fun invoke(taskCategoryModel: TaskCategoryModel) {
        categoryRepository.delete(taskCategoryModel)
    }
}