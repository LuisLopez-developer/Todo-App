package com.example.todoapp.holidays.data

import com.example.todoapp.holidays.data.network.HoliDaysService
import com.example.todoapp.holidays.data.network.response.holiDaysResponse

class HoliDaysRepository {
    private val api = HoliDaysService()

    // Obtener todas las festividades
    suspend fun holidays(): List<holiDaysResponse>? {
        return api.holidays()
    }

    // Obtener festividad por fecha específica
    suspend fun holidayByDate(fecha: String): holiDaysResponse? {
        return api.holidayByDate(fecha)
    }
}
