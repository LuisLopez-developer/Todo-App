package com.example.todoapp.addtasks.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.todoapp.addtasks.domain.model.TaskItem
import com.example.todoapp.settings.auth.data.UserEntity
import com.example.todoapp.state.data.StateEntity
import com.example.todoapp.state.data.constants.DefaultStateId.ACTIVE_ID
import com.example.todoapp.taskcategory.data.CategoryEntity
import kotlinx.serialization.Contextual
import org.threeten.bp.LocalDate
import org.threeten.bp.LocalTime
import org.threeten.bp.OffsetDateTime
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
        ),
        ForeignKey(
            entity = StateEntity::class,
            parentColumns = ["id"],
            childColumns = ["stateId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("categoryId"), Index("userId"), Index("stateId")]
)
data class TaskEntity(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    val task: String,
    var selected: Boolean = false,
    @Contextual val startDate: LocalDate = LocalDate.now(),
    @Contextual val endDate: LocalDate? = null,
    @Contextual val time: LocalTime? = null,
    val details: String? = null,
    val categoryId: String? = null,
    val userId: String? = null,
    val stateId: String = ACTIVE_ID,
    @Contextual val createdAt: OffsetDateTime = OffsetDateTime.now(),
    @Contextual val updatedAt: OffsetDateTime = OffsetDateTime.now(),
)

fun TaskItem.toDatabase() = TaskEntity(
    id = id,
    task = task,
    selected = selected,
    startDate = starDate,
    endDate = endDate,
    time = time,
    details = details,
    categoryId = categoryId,
    userId = userId,
    stateId = stateId,
    createdAt = createdAt,
    updatedAt = updatedAt
)