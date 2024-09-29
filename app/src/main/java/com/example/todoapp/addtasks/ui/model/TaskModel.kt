package com.example.todoapp.addtasks.ui.model

import org.threeten.bp.LocalDate
import org.threeten.bp.LocalTime
import java.util.UUID

data class TaskModel(
    val id: String = UUID.randomUUID().toString(), //Obtener la fecha actual incluido los milisegundos
    val task: String,
    val selected: Boolean = false,
    val startDate: LocalDate = LocalDate.now(),
    val endDate: LocalDate? = null,
    val time: LocalTime? = null,
    val details: String? = null,
    val categoryId: String? = null
)