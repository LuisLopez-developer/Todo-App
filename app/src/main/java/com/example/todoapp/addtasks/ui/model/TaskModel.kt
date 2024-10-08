package com.example.todoapp.addtasks.ui.model

import com.example.todoapp.addtasks.domain.model.TaskItem
import com.example.todoapp.state.data.constants.DefaultStateId.ACTIVE_ID
import org.threeten.bp.LocalDate
import org.threeten.bp.LocalTime
import org.threeten.bp.OffsetDateTime
import java.util.UUID

data class TaskModel(
    val id: String = UUID.randomUUID()
        .toString(), //Obtener la fecha actual incluido los milisegundos
    val task: String,
    val selected: Boolean = false,
    val startDate: LocalDate = LocalDate.now(),
    val endDate: LocalDate? = null,
    val time: LocalTime? = null,
    val details: String? = null,
    val categoryId: String? = null,
    val userId: String? = null,
    val stateId: String = ACTIVE_ID,
    val createdAt: OffsetDateTime = OffsetDateTime.now(),
    val updatedAt: OffsetDateTime = OffsetDateTime.now(),
)

fun TaskItem.toViewModel() = TaskModel(
    id = id,
    task = task,
    selected = selected,
    startDate = startDate,
    endDate = endDate,
    time = time,
    details = details,
    categoryId = categoryId,
    userId
)

fun List<TaskItem>.toViewModelList(): List<TaskModel> = this.map { it.toViewModel() }