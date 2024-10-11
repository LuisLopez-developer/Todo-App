package com.example.todoapp.user.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.todoapp.user.domain.model.UserItem

@Entity
data class UserEntity(
    @PrimaryKey val uid: String,
    val name: String,
    val email: String
)

fun UserItem.toDatabase(): UserEntity {
    return UserEntity(
        uid = uid,
        name = name,
        email = email
    )
}