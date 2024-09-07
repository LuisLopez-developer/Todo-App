package com.example.todoapp.addtasks.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate
import java.time.LocalTime

@Entity
data class TaskEntity(
    @PrimaryKey
    val id: Int,
    val task: String,
    var selected: Boolean = false,
    val startDate: LocalDate,  // Fecha de inicio
    val endDate: LocalDate,    // Fecha de fin
    val time: LocalTime        // Hora
)

