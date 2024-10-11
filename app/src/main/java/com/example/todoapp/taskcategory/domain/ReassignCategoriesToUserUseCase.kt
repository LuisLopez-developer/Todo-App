package com.example.todoapp.taskcategory.domain

import com.example.todoapp.taskcategory.data.CategoryRepository
import com.example.todoapp.utils.Logger
import javax.inject.Inject

class ReassignCategoriesToUserUseCase @Inject constructor(
    private val categoryRepository: CategoryRepository,
) {
    suspend operator fun invoke(newUserId: String?) {
        try {
            categoryRepository.reassignCategoriesToUser(newUserId)
        } catch (e: Exception) {
            Logger.error("ReassignCategoriesToUserUseCase", e.message.orEmpty())
        }
    }
}