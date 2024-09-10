package com.example.todoapp.holidays.domain

import com.example.todoapp.holidays.data.HoliDaysRepository
import com.example.todoapp.holidays.data.network.response.HoliDaysResponse

class HoliDaysUseCase {
    private val repository = HoliDaysRepository()

    suspend operator fun invoke(): String {
        val holidays: List<HoliDaysResponse>? = repository.Holidays()

        return if (holidays != null) {
            holidays.joinToString(separator = "\n") { holiday ->
                "Fecha: ${holiday.fecha}, Nombre: ${holiday.nombre}"
            }
        } else {
            "No se pudieron obtener los días festivos"
        }
    }

    suspend fun HolidayByDate(fecha: String): String {
        val holiday: HoliDaysResponse? = repository.HolidayByDate(fecha)

        return if (holiday != null) {
            "Fecha: ${holiday.fecha}, Nombre: ${holiday.nombre}"
        } else {
            "No se encontró ninguna festividad para la fecha: $fecha"
        }
    }
}
