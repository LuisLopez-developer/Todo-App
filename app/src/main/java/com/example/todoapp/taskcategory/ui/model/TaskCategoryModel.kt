package com.example.todoapp.taskcategory.ui.model

import com.example.todoapp.state.data.constants.DefaultStateId.ACTIVE_ID
import com.example.todoapp.taskcategory.domain.model.CategoryItem
import org.threeten.bp.OffsetDateTime
import java.util.UUID

data class TaskCategoryModel(
    val id: String = UUID.randomUUID().toString(),
    val category: String,
    val userId: String? = null,
    val stateId: String = ACTIVE_ID,
    val createdAt: OffsetDateTime = OffsetDateTime.now(),
    val updatedAt: OffsetDateTime = OffsetDateTime.now(),
)

fun CategoryItem.toViewModel() =
    TaskCategoryModel(id, category, userId, stateId, createdAt, updatedAt)

fun List<CategoryItem>.toViewModelList(): List<TaskCategoryModel> = this.map { it.toViewModel() }