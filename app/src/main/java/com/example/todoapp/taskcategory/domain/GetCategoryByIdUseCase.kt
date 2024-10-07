package com.example.todoapp.taskcategory.domain

import com.example.todoapp.taskcategory.data.CategoryRepository
import com.example.todoapp.taskcategory.domain.model.CategoryItem
import javax.inject.Inject

class GetCategoryByIdUseCase @Inject constructor(private val categoryRepository: CategoryRepository) {
    suspend operator fun invoke(categoryId: String): CategoryItem? {
        return categoryRepository.getCategoryById(categoryId)
    }
}