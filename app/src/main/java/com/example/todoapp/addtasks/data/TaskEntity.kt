package com.example.todoapp.addtasks.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import org.threeten.bp.LocalDate
import org.threeten.bp.LocalTime

@Entity
data class TaskEntity(
    @PrimaryKey
    val id: Int,
    val task: String,
    var selected:Boolean = false,
    val startDate: LocalDate? = null,  // Fecha de inicio opcional
    val endDate: LocalDate? = null,    // Fecha de fin opcional
    val time: LocalTime? = null,       // Hora opcional
    val details: String? = null     // Detalles opcionales
)
