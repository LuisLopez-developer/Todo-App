package com.example.todoapp.taskcategory.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.todoapp.state.data.StateEntity
import com.example.todoapp.state.data.constants.DefaultStateId.ACTIVE_ID
import com.example.todoapp.taskcategory.domain.model.CategoryItem
import com.example.todoapp.user.data.UserEntity
import kotlinx.serialization.Contextual
import org.threeten.bp.OffsetDateTime
import java.util.UUID

@Entity(
    foreignKeys = [
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
    indices = [Index("category", unique = true), Index("userId"), Index("stateId")]
)
data class CategoryEntity(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    val category: String = "",
    val userId: String? = null,
    val stateId: String = ACTIVE_ID,
    @Contextual val createdAt: OffsetDateTime = OffsetDateTime.now(),
    @Contextual val updatedAt: OffsetDateTime = OffsetDateTime.now(),
)

fun CategoryItem.toDatabase() = CategoryEntity(id, category, userId, stateId, createdAt, updatedAt)