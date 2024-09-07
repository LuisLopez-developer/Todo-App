package com.example.todoapp.addtasks.ui.model

import java.time.LocalDate
import java.time.LocalTime

data class TaskModel(
    val id: Int = System.currentTimeMillis().hashCode(), // Obtener la fecha actual incluido los milisegundos
    val task: String,
    var selected: Boolean = false,
    val startDate: LocalDate,  // Fecha de inicio
    val endDate: LocalDate,    // Fecha de fin
    val time: LocalTime        // Hora
)
