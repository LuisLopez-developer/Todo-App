package com.example.todoapp.user.ui.model

import com.example.todoapp.user.domain.model.UserItem

data class UserModel(
    val id: String,
    val name: String,
    val email: String
)

fun UserItem.toViewModel(): UserModel = UserModel(uid, name, email)