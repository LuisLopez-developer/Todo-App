package com.example.todoapp.addtasks.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.example.todoapp.addtasks.utils.LocalDateSerializer
import com.example.todoapp.addtasks.utils.LocalTimeSerializer
import com.example.todoapp.taskcategory.data.CategoryEntity
import org.threeten.bp.LocalDate
import org.threeten.bp.LocalTime

@Entity(
    foreignKeys = [ForeignKey(
        entity = CategoryEntity::class,
        parentColumns = ["id"],
        childColumns = ["categoryId"],
        onDelete = ForeignKey.CASCADE
    )]
)
data class TaskEntity(
    @PrimaryKey
    val id: Int,
    val task: String,
    var selected: Boolean = false,
    val startDate: LocalDate = LocalDate.now(),
    val endDate: LocalDate? = null,
    val time: LocalTime? = null,
    val details: String? = null,
    val categoryId: Int? = null
) {
    // No-argument constructor for Firestore
    constructor() : this(0, "")

    fun toMap(): Map<String, Any?> {
        return mapOf(
            "id" to id,
            "task" to task,
            "selected" to selected,
            "startDate" to LocalDateSerializer.serialize(startDate),
            "endDate" to LocalDateSerializer.serialize(endDate),
            "time" to LocalTimeSerializer.serialize(time),
            "details" to details,
            "categoryId" to categoryId
        )
    }

    companion object {
        fun fromMap(map: Map<String, Any?>): TaskEntity {
            return TaskEntity(
                id = (map["id"] as? Long)?.toInt() ?: 0,
                task = map["task"] as String,
                selected = map["selected"] as Boolean,
                startDate = LocalDateSerializer.deserialize(map["startDate"] as? String ?: (map["startDate"] as? HashMap<*, *>)?.get("dateString") as? String) ?: LocalDate.now(),
                endDate = LocalDateSerializer.deserialize(map["endDate"] as? String ?: (map["endDate"] as? HashMap<*, *>)?.get("dateString") as? String),
                time = LocalTimeSerializer.deserialize(map["time"] as? String ?: (map["time"] as? HashMap<*, *>)?.get("timeString") as? String),
                details = map["details"] as? String,
                categoryId = (map["categoryId"] as? Long)?.toInt()
            )
        }
    }
}