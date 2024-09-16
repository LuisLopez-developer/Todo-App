package com.example.todoapp.settings.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class UserEntity(
    @PrimaryKey val uid: String,
    val name: String,
    val email: String
)