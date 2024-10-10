package com.example.todoapp.holidays.ui.model

import com.example.todoapp.holidays.domain.model.HolidayItem
import org.threeten.bp.LocalDate

data class HolidayModel(
    val date: LocalDate,
    val name: String,
)

fun HolidayItem.toViewModel() = HolidayModel(
    date = date,
    name = name
)

fun List<HolidayItem>.toViewModelList() = map { it.toViewModel() }