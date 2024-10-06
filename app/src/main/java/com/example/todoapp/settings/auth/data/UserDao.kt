package com.example.todoapp.settings.auth.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addUser(user: UserEntity)

    @Query("SELECT * FROM UserEntity LIMIT 1")
    fun getUser(): Flow<UserEntity?>

    @Query("DELETE FROM UserEntity")
    suspend fun deleteAllUsers()

    @Query("SELECT COUNT(*) > 0 FROM UserEntity WHERE uid = :userId")
    suspend fun doesUserExists(userId: String): Boolean
}