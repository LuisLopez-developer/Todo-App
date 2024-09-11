package com.example.todoapp.addtasks.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {

    @Query("SELECT * FROM TaskEntity")
    fun getTasks(): Flow<List<TaskEntity>>

    @Insert
    suspend fun addTask(item: TaskEntity)

    @Update
    suspend fun updateTask(item: TaskEntity)

    @Delete
    suspend fun deleteTask(item: TaskEntity)

    @Query("SELECT * FROM TaskEntity WHERE id = :taskId")
    suspend fun getTaskById(taskId: Int): TaskEntity?

    @Query("SELECT * FROM TaskEntity WHERE startDate = :date ORDER BY id DESC")
    fun getTasksByDate(date: String): Flow<List<TaskEntity>>
}