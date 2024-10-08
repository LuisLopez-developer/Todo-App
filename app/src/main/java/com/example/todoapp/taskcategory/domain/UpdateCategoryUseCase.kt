package com.example.todoapp.taskcategory.domain

import com.example.todoapp.taskcategory.data.CategoryRepository
import com.example.todoapp.taskcategory.data.toDatabase
import com.example.todoapp.taskcategory.domain.model.CategoryItem
import javax.inject.Inject

class UpdateCategoryUseCase @Inject constructor(private val categoryRepository: CategoryRepository) {
    suspend operator fun invoke(categoryItem: CategoryItem) {
        if (categoryRepository.isCategoryNameValid(categoryItem.category)) {
            return
        }
        categoryRepository.update(categoryItem.toDatabase())
    }
}