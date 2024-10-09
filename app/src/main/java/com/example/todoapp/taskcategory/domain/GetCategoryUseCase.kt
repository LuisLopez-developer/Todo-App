package com.example.todoapp.taskcategory.domain

import com.example.todoapp.taskcategory.data.CategoryRepository
import com.example.todoapp.taskcategory.domain.model.CategoryItem
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetCategoryUseCase @Inject constructor(private val categoryRepository: CategoryRepository) {
    operator fun invoke(): Flow<List<CategoryItem>> = categoryRepository.categories
}