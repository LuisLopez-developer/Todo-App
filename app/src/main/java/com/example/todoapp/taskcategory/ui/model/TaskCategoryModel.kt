package com.example.todoapp.taskcategory.ui.model

import java.util.UUID

data class TaskCategoryModel(
    val id: String = UUID.randomUUID().toString(),
    val category: String,
    val userId: String? = null
)
