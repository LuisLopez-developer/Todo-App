package com.example.todoapp.addtasks.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.example.todoapp.taskcategory.data.CategoryEntity
import org.threeten.bp.LocalDate
import org.threeten.bp.LocalTime

@Entity(
    foreignKeys = [ForeignKey(
        entity = CategoryEntity::class,
        parentColumns = ["id"],
        childColumns = ["categoryId"],
        onDelete = ForeignKey.CASCADE  // Puedes especificar qué hacer al eliminar una categoría
    )]
)
data class TaskEntity(
    @PrimaryKey
    val id: Int,
    val task: String,
    var selected: Boolean = false,
    val startDate: LocalDate = LocalDate.now(),
    val endDate: LocalDate? = null,    // Fecha de fin opcional
    val time: LocalTime? = null,       // Hora opcional
    val details: String? = null,     // Detalles opcionales
    val categoryId: Int? = null // Relación con CategoryEntity
)
