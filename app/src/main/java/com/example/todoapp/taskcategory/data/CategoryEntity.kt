package com.example.todoapp.taskcategory.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.todoapp.settings.auth.data.UserEntity
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
        )
    ],
    indices = [Index("userId")]
)
data class CategoryEntity(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    val category: String = "",
    val userId: String? = null,
    @Contextual val createdAt: OffsetDateTime = OffsetDateTime.now(),
    @Contextual val updatedAt: OffsetDateTime = OffsetDateTime.now()
)