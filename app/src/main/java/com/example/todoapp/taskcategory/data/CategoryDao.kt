package com.example.todoapp.taskcategory.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface CategoryDao {
    @Query("SELECT * FROM CategoryEntity")
    fun getCategory(): Flow<List<CategoryEntity>>

    @Query("SELECT * FROM CategoryEntity WHERE id = :categoryId LIMIT 1")
    suspend fun getCategoryById(categoryId: Int): CategoryEntity?

    @Insert
    suspend fun addCategory(item: CategoryEntity)

    @Update
    suspend fun updateCategory(item: CategoryEntity)

    @Delete
    suspend fun deleteCategory(item: CategoryEntity)
}