package com.example.todoapp.taskcategory.ui.model

import com.example.todoapp.state.data.constants.DefaultStateId.ACTIVE_ID
import java.util.UUID

data class TaskCategoryModel(
    val id: String = UUID.randomUUID().toString(),
    val category: String,
    val userId: String? = null,
    val stateId: String = ACTIVE_ID
)
