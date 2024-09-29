package com.example.todoapp.taskcategory.data


import com.example.todoapp.taskcategory.ui.model.TaskCategoryModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CategoryRepository @Inject constructor(private val categoryDao: CategoryDao) {

    val categories: Flow<List<TaskCategoryModel>> = categoryDao.getCategory().map { items ->
        items.map {
            TaskCategoryModel(it.id, it.category)
        }
    }

    suspend fun add(taskCategoryModel: TaskCategoryModel) {
        categoryDao.addCategory(taskCategoryModel.toData())
    }

    suspend fun update(taskCategoryModel: TaskCategoryModel) {
        categoryDao.updateCategory(taskCategoryModel.toData())
    }

    suspend fun delete(taskCategoryModel: TaskCategoryModel) {
        categoryDao.deleteCategory(taskCategoryModel.toData())
    }

}

fun TaskCategoryModel.toData(): CategoryEntity {
    return CategoryEntity(this.id, this.category, this.userId)
}