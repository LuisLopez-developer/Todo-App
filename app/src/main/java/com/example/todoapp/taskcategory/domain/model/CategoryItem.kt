package com.example.todoapp.taskcategory.domain.model

import com.example.todoapp.taskcategory.data.CategoryEntity
import com.example.todoapp.taskcategory.ui.model.TaskCategoryModel
import org.threeten.bp.OffsetDateTime

data class CategoryItem(
    val id: String,
    val category: String,
    val userId: String?,
    val stateId: String,
    val createdAt: OffsetDateTime,
    val updatedAt: OffsetDateTime,
)

fun TaskCategoryModel.toDomain() = CategoryItem(id, category, userId, stateId, createdAt, updatedAt)

fun CategoryEntity.toDomain() = CategoryItem(id, category, userId, stateId, createdAt, updatedAt)

