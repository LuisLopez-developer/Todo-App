package com.example.todoapp.state.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface StateDao {

    @Insert
    suspend fun addState(item: StateEntity)

    @Query("SELECT * FROM StateEntity WHERE id = :stateId")
    suspend fun getStateById(stateId: String): StateEntity?
}