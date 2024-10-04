package com.example.todoapp.taskcategory.data

import com.example.todoapp.settings.auth.data.UserDao
import com.example.todoapp.taskcategory.ui.model.TaskCategoryModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CategoryRepository @Inject constructor(
    private val categoryDao: CategoryDao,
    private val userDao: UserDao,
) {

    val categories: Flow<List<TaskCategoryModel>> = categoryDao.getCategory().map { items ->
        items.map {
            TaskCategoryModel(it.id, it.category, stateId = it.stateId)
        }
    }

    suspend fun add(taskCategoryModel: TaskCategoryModel) {
        categoryDao.addCategory(
            taskCategoryModel.toData().copy(userId = userDao.getUser().map { it?.uid }.first())
        )
    }

    suspend fun update(taskCategoryModel: TaskCategoryModel) {
        categoryDao.updateCategory(taskCategoryModel.toData())
    }

    suspend fun delete(taskCategoryModel: TaskCategoryModel) {
        categoryDao.deleteCategory(taskCategoryModel.toData())
    }

}

fun TaskCategoryModel.toData(): CategoryEntity {
    return CategoryEntity(this.id, this.category, this.userId, this.stateId)
}