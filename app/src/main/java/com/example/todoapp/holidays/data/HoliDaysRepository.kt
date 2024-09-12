package com.example.todoapp.holidays.data

import com.example.todoapp.holidays.data.network.HoliDaysService
import com.example.todoapp.holidays.data.network.response.holiDaysResponse
import com.example.todoapp.holidays.ui.model.HolidaysModel

class HoliDaysRepository {
    private val api = HoliDaysService()

    // Obtener todas las festividades
    suspend fun holidays(): List<HolidaysModel>? {
        // Devolver la transformación de holiDaysResponse a HolidaysModel
        return api.holidays()?.map { items ->
            HolidaysModel(items.fecha, items.nombre)
        }
    }

    // Obtener festividad por fecha específica
    suspend fun holidayByDate(fecha: String): HolidaysModel? {
        // Devolver la transformación de holiDaysResponse a HolidaysModel
        return api.holidayByDate(fecha).let { items ->
            items?.let { HolidaysModel(items.fecha, it.nombre) }
        }
    }
}
