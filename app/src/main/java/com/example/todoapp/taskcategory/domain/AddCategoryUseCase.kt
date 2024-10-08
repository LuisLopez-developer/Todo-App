package com.example.todoapp.taskcategory.domain

import com.example.todoapp.settings.auth.domain.GetUserUseCase
import com.example.todoapp.taskcategory.data.CategoryRepository
import com.example.todoapp.taskcategory.data.toDatabase
import com.example.todoapp.taskcategory.domain.model.CategoryItem
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class AddCategoryUseCase @Inject constructor(
    private val categoryRepository: CategoryRepository,
    private val getUserUseCase: GetUserUseCase,
) {
    suspend operator fun invoke(categoryItem: CategoryItem) {
        if (categoryRepository.isCategoryNameValid(categoryItem.category)) {
            return
        }
        // recuperar el id del usuario actual
        categoryRepository.add(
            categoryItem.toDatabase().copy(userId = getUserUseCase.invoke().first()?.id)
        )
    }

}