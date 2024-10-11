package com.example.todoapp.taskcategory.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.todoapp.state.data.constants.DefaultStateId.ACTIVE_ID
import com.example.todoapp.state.data.constants.DefaultStateId.DELETED_ID
import kotlinx.coroutines.flow.Flow

@Dao
interface CategoryDao {
    @Query("SELECT * FROM CategoryEntity")
    fun getCategory(): Flow<List<CategoryEntity>>

    @Query("SELECT * FROM CategoryEntity WHERE stateId = :activeStateId")
    fun getActiveCategory(activeStateId: String = ACTIVE_ID): Flow<List<CategoryEntity>>

    @Query("SELECT * FROM CategoryEntity WHERE id = :categoryId LIMIT 1")
    suspend fun getCategoryById(categoryId: String): CategoryEntity?

    @Insert
    suspend fun addCategory(item: CategoryEntity)

    @Update
    suspend fun updateCategory(item: CategoryEntity)

    @Delete
    suspend fun deleteCategory(item: CategoryEntity)

    @Query("UPDATE CategoryEntity SET stateId = :deletedStateId, category = :newCategoryName WHERE id = :categoryId")
    suspend fun deleteCategoryLogically(categoryId: String, newCategoryName: String, deletedStateId: String = DELETED_ID)

    @Query("SELECT COUNT(*) > 0 FROM CategoryEntity WHERE category = :categoryName")
    suspend fun isCategoryNameValid(categoryName: String): Boolean

    @Query("UPDATE CategoryEntity SET userId = :newUserId")
    suspend fun reassignCategoriesToUser(newUserId: String?)
}