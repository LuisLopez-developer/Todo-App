package com.example.todoapp.addtasks.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.todoapp.state.data.constants.DefaultStateId.ACTIVE_ID
import com.example.todoapp.state.data.constants.DefaultStateId.DELETED_ID
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {

    @Query("SELECT * FROM TaskEntity")
    fun getTasks(): Flow<List<TaskEntity>>

    @Query("SELECT * FROM TaskEntity WHERE stateId = :activeStateId")
    fun getActiveTasks(activeStateId: String = ACTIVE_ID): Flow<List<TaskEntity>>

    @Insert
    suspend fun addTask(item: TaskEntity)

    @Update
    suspend fun updateTask(item: TaskEntity)

    @Delete
    suspend fun deleteTask(item: TaskEntity)

    @Query("UPDATE TaskEntity SET stateId = :deletedStateId WHERE categoryId = :categoryId")
    suspend fun deleteTasksByCategoryLogically(categoryId: String, deletedStateId: String = DELETED_ID)

    @Query("SELECT * FROM TaskEntity WHERE id = :taskId")
    suspend fun getTaskById(taskId: String): TaskEntity?

    @Query("SELECT * FROM TaskEntity WHERE id = :taskId")
    fun getTaskByIdFlow(taskId: String): Flow<TaskEntity>

    @Query("SELECT * FROM TaskEntity WHERE startDate = :date ORDER BY id DESC")
    fun getTasksByDate(date: String): Flow<List<TaskEntity>>

    @Query("SELECT * FROM taskentity WHERE startDate = :date AND stateId = :activeStateId ORDER BY id DESC")
    fun getActiveTasksByDate(date: String, activeStateId: String = ACTIVE_ID): Flow<List<TaskEntity>>

    // Aquí cambiamos category por categoryId, que es el id de la entidad CategoryEntity
    @Query("SELECT * FROM TaskEntity WHERE categoryId = :categoryId ORDER BY id DESC")
    fun getTasksByCategory(categoryId: String): Flow<List<TaskEntity>>
}
