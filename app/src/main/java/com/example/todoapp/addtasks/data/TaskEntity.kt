package com.example.todoapp.addtasks.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import org.threeten.bp.LocalDate
import org.threeten.bp.LocalTime
import java.util.Date

@Entity
data class TaskEntity(
    @PrimaryKey
    val id: Int,
    val task: String,
    var selected:Boolean = false,
    val startDate: LocalDate = LocalDate.now(),
    val endDate: LocalDate? = null,    // Fecha de fin opcional
    val time: LocalTime? = null,       // Hora opcional
    val details: String? = null,     // Detalles opcionales
    val category: String?     // Categoría opcional
)
