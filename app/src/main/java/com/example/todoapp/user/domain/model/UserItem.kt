package com.example.todoapp.user.domain.model

import com.example.todoapp.user.data.UserEntity

data class UserItem(
    val uid: String,
    val name: String,
    val email: String,
)

fun UserEntity.toDomain(): UserItem = UserItem(uid, name, email)

