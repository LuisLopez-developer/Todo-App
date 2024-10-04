package com.example.todoapp.state.data

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(
    indices = [Index("state", unique = true)]
)
data class StateEntity(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    val state: String
)
