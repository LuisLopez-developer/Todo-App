package com.example.todoapp.taskcategory.data

import com.example.todoapp.taskcategory.domain.model.CategoryItem
import com.example.todoapp.taskcategory.domain.model.toDomain
import com.example.todoapp.taskcategory.ui.model.TaskCategoryModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CategoryRepository @Inject constructor(private val categoryDao: CategoryDao) {

    val categories: Flow<List<TaskCategoryModel>> = categoryDao.getActiveCategory().map { items ->
        items.map { it.toCategoryModel() }
    }

    val allCategories: Flow<List<CategoryItem>> = categoryDao.getCategory().map { items ->
        items.map { it.toDomain() }
    }

    suspend fun getCategoryById(categoryId: String): CategoryItem? {
        return categoryDao.getCategoryById(categoryId)?.toDomain()
    }

    suspend fun isCategoryNameValid(categoryName: String) =
        categoryDao.isCategoryNameValid(categoryName)

    suspend fun add(taskCategoryModel: TaskCategoryModel) {
        categoryDao.addCategory(taskCategoryModel.toCategoryEntity())
    }

    suspend fun deleteCategoryLogically(categoryId: String, newCategoryName: String) {
        categoryDao.deleteCategoryLogically(categoryId, newCategoryName)
    }

    suspend fun update(taskCategoryModel: TaskCategoryModel) {
        categoryDao.updateCategory(taskCategoryModel.toCategoryEntity())
    }

    suspend fun delete(taskCategoryModel: TaskCategoryModel) {
        categoryDao.deleteCategory(taskCategoryModel.toCategoryEntity())
    }

}

fun TaskCategoryModel.toCategoryEntity(): CategoryEntity {
    return CategoryEntity(this.id, this.category, this.userId, this.stateId)
}

fun CategoryEntity.toCategoryModel(): TaskCategoryModel {
    return TaskCategoryModel(this.id, this.category, this.stateId)
}