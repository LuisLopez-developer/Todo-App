package com.example.todoapp.holidays.domain

import com.example.todoapp.holidays.data.HoliDaysRepository
import com.example.todoapp.holidays.data.network.response.holiDaysResponse

class HoliDaysUseCase {
    private val repository = HoliDaysRepository()

    suspend operator fun invoke(): String {
        val holidays: List<holiDaysResponse>? = repository.holidays()

        return if (holidays != null) {
            holidays.joinToString(separator = "\n") { holiday ->
                "Fecha: ${holiday.fecha}, Nombre: ${holiday.nombre}"
            }
        } else {
            "No se pudieron obtener los días festivos"
        }
    }

    suspend fun HolidayByDate(fecha: String): String {
        val holiday: holiDaysResponse? = repository.holidayByDate(fecha)

        return if (holiday != null) {
            "Fecha: ${holiday.fecha}, Nombre: ${holiday.nombre}"
        } else {
            "No se encontró ninguna festividad para la fecha: $fecha"
        }
    }
}
