package com.example.todoapp.holidays.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.todoapp.holidays.domain.model.HolidayItem
import org.threeten.bp.LocalDate

@Entity(tableName = "holidays")
data class HolidayEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val date: LocalDate,
    val name: String
)

fun HolidayItem.toDatabase() = HolidayEntity(
    date = date,
    name = name
)

fun List<HolidayItem>.toDatabaseList() = map { it.toDatabase() }