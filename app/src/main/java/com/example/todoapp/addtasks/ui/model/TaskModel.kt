package com.example.todoapp.addtasks.ui.model

import org.threeten.bp.LocalDate
import org.threeten.bp.LocalTime

data class TaskModel(
    val id: Int = System.currentTimeMillis().hashCode(), //Obtener la fecha actual incluido los milisegundos
    val task: String,
    var selected: Boolean = false,
    val startDate: LocalDate = LocalDate.now(),
    val endDate: LocalDate? = null,
    val time: LocalTime? = null,
    val details: String? = null,
    val category: String? = null
)