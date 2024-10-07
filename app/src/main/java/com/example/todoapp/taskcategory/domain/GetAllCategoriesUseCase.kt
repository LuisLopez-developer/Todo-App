package com.example.todoapp.taskcategory.domain

import com.example.todoapp.taskcategory.data.CategoryRepository
import javax.inject.Inject

class GetAllCategoriesUseCase @Inject constructor(private val categoryRepository: CategoryRepository) {
    operator fun invoke() = categoryRepository.allCategories
}