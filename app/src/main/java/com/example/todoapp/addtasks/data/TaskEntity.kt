package com.example.todoapp.addtasks.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class TaskEntity(
    @PrimaryKey
    val id: Int,
    val task: String,
    var selected:Boolean = false,
    val startDate: String? = null,  // Fecha de inicio opcional
    val endDate: String? = null,    // Fecha de fin opcional
    val time: String? = null,       // Hora opcional
    val details: String? = null     // Detalles opcionales
)
