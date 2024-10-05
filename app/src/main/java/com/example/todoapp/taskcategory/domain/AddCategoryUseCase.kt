package com.example.todoapp.taskcategory.domain

import com.example.todoapp.settings.auth.domain.GetUserUseCase
import com.example.todoapp.taskcategory.data.CategoryRepository
import com.example.todoapp.taskcategory.ui.model.TaskCategoryModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class AddCategoryUseCase @Inject constructor(
    private val categoryRepository: CategoryRepository,
    private val getUserUseCase: GetUserUseCase,
) {
    suspend operator fun invoke(taskCategoryModel: TaskCategoryModel) {
        if (categoryRepository.isCategoryNameValid(taskCategoryModel.category)) {
            return
        }
        // recuperar el id del usuario actual
        categoryRepository.add(taskCategoryModel.copy(userId = getUserUseCase.invoke().first()?.id))
    }

}