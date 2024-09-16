package com.example.todoapp.taskcategory.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class CategoryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val category: String = ""
) {
    // No-argument constructor for Firestore
    constructor() : this(0, "")
}