package com.example.todoapp.holidays.domain.model

import com.example.todoapp.holidays.data.local.HolidayEntity
import com.example.todoapp.holidays.data.network.response.HoliDaysResponse
import org.threeten.bp.LocalDate

data class HolidayItem(
    val date: LocalDate,
    val name: String,
)

fun List<HoliDaysResponse>.toDomainList(): List<HolidayItem> = this.map { it.toDomain() }

fun HoliDaysResponse.toDomain() = HolidayItem(
    date = LocalDate.parse(fecha),
    name = nombre
)

fun List<HolidayEntity>.toDomainEntityList(): List<HolidayItem> = this.map { it.toDomain() }

fun HolidayEntity.toDomain() = HolidayItem(
    date = date,
    name = name
)