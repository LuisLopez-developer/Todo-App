package com.example.todoapp.holidays.data

import com.example.todoapp.holidays.data.network.HoliDaysService
import com.example.todoapp.holidays.data.network.response.HoliDaysResponse

class HoliDaysRepository {
    private val api = HoliDaysService()

    // Obtener todas las festividades
    suspend fun Holidays(): List<HoliDaysResponse>? {
        return api.Holidays()
    }

    // Obtener festividad por fecha específica
    suspend fun HolidayByDate(fecha: String): HoliDaysResponse? {
        return api.HolidayByDate(fecha)
    }
}
