package com.example.todoapp.addtasks.domain.model

import com.example.todoapp.addtasks.data.TaskEntity
import com.example.todoapp.addtasks.ui.model.TaskModel
import org.threeten.bp.LocalDate
import org.threeten.bp.LocalTime
import org.threeten.bp.OffsetDateTime

data class TaskItem(
    val id: String,
    val task: String,
    val startDate: LocalDate,
    val endDate: LocalDate?,
    val time: LocalTime?,
    val selected: Boolean,
    val details: String?,
    val categoryId: String?,
    val userId: String?,
    val stateId: String,
    val createdAt: OffsetDateTime,
    val updatedAt: OffsetDateTime,
)

fun TaskEntity.toDomain() = TaskItem(
    id = id,
    task = task,
    startDate = startDate,
    endDate = endDate,
    time = time,
    selected = selected,
    details = details,
    categoryId = categoryId,
    userId = userId,
    stateId = stateId,
    createdAt = createdAt,
    updatedAt = updatedAt
)

fun List<TaskEntity>.toListDomain(): List<TaskItem> = this.map { it.toDomain() }

fun TaskModel.toDomain() = TaskItem(
    id = id,
    task = task,
    startDate = startDate,
    endDate = endDate,
    time = time,
    selected = selected,
    details = details,
    categoryId = categoryId,
    userId = userId,
    stateId = stateId,
    createdAt = createdAt,
    updatedAt = updatedAt
)