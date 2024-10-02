package com.example.todoapp.addtasks.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.example.todoapp.addtasks.utils.LocalDateSerializer
import com.example.todoapp.addtasks.utils.LocalTimeSerializer
import com.example.todoapp.settings.auth.data.UserEntity
import com.example.todoapp.taskcategory.data.CategoryEntity
import org.threeten.bp.LocalDate
import org.threeten.bp.LocalTime
import java.util.UUID

@Entity(
    foreignKeys = [
        ForeignKey(
        entity = CategoryEntity::class,
        parentColumns = ["id"],
        childColumns = ["categoryId"],
        onDelete = ForeignKey.CASCADE
    ),
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["uid"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class TaskEntity(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    val task: String,
    var selected: Boolean = false,
    val startDate: LocalDate = LocalDate.now(),
    val endDate: LocalDate? = null,
    val time: LocalTime? = null,
    val details: String? = null,
    val categoryId: String? = null,
    val userId: String? = null,
) {
    // Convertir un "TaskEntity" en un "Map<String, Any?>"
    fun toMap(): Map<String, Any?> = mapOf(
        "id" to id,
        "task" to task,
        "selected" to selected,
        "startDate" to LocalDateSerializer.serialize(startDate),
        "endDate" to LocalDateSerializer.serialize(endDate),
        "time" to LocalTimeSerializer.serialize(time),
        "details" to details,
        "categoryId" to categoryId,
        "userId" to userId
    )

    // Convertir un "Map<String, Any?>" en un "TaskEntity"
    companion object {
        fun fromMap(map: Map<String, Any?>): TaskEntity = TaskEntity(
            id = map["id"] as String,
            task = map["task"] as String,
            selected = map["selected"] as Boolean,
            startDate = LocalDateSerializer.deserialize(map["startDate"] as? String)
                ?: LocalDate.now(),
            endDate = LocalDateSerializer.deserialize(map["endDate"] as? String),
            time = LocalTimeSerializer.deserialize(map["time"] as? String),
            details = map["details"] as? String,
            categoryId = map["categoryId"] as? String,
            userId = map["userId"] as? String
        )
    }
}