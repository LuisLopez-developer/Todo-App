package com.example.todoapp.taskcategory.data

import com.example.todoapp.taskcategory.domain.model.CategoryItem
import com.example.todoapp.taskcategory.domain.model.toDomain
import com.example.todoapp.taskcategory.domain.model.toDomainList
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CategoryRepository @Inject constructor(private val categoryDao: CategoryDao) {

    val categories: Flow<List<CategoryItem>> =
        categoryDao.getActiveCategory().map { it.toDomainList() }

    val allCategories: Flow<List<CategoryItem>> = categoryDao.getCategory().map {
        it.toDomainList()
    }

    suspend fun getCategoryById(categoryId: String): CategoryItem? {
        return categoryDao.getCategoryById(categoryId)?.toDomain()
    }

    suspend fun isCategoryNameValid(categoryName: String) =
        categoryDao.isCategoryNameValid(categoryName)

    suspend fun add(categoryEntity: CategoryEntity) {
        categoryDao.addCategory(categoryEntity)
    }

    suspend fun deleteCategoryLogically(categoryId: String, newCategoryName: String) {
        categoryDao.deleteCategoryLogically(categoryId, newCategoryName)
    }

    suspend fun update(categoryEntity: CategoryEntity) {
        categoryDao.updateCategory(categoryEntity)
    }

    suspend fun delete(categoryEntity: CategoryEntity) {
        categoryDao.deleteCategory(categoryEntity)
    }

    suspend fun reassignCategoriesToUser(newUserId: String?) {
        categoryDao.reassignCategoriesToUser(newUserId)
    }
}