package com.example.todoapp.holidays.ui.model

import org.threeten.bp.LocalDate

data class HolidayModel(
    val date: LocalDate,
    val name: String,
)