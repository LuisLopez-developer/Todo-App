package com.example.todoapp.holidays.data

import com.example.todoapp.holidays.data.network.HoliDaysService
import com.example.todoapp.holidays.ui.model.HolidayModel
import org.threeten.bp.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HoliDaysRepository @Inject constructor(private val api: HoliDaysService) {

    // Obtener todas las festividades
    suspend fun holidays(): List<HolidayModel>? {
        // Devolver la transformación de holiDaysResponse a HolidaysModel
        return api.holidays()?.map { items ->
            HolidayModel(LocalDate.parse(items.fecha), items.nombre)
        }
    }

    // Obtener festividad por fecha específica
    suspend fun holidayByDate(fecha: String): HolidayModel? {
        // Devolver la transformación de holiDaysResponse a HolidaysModel
        return api.holidayByDate(fecha).let { items ->
            items?.let { HolidayModel(LocalDate.parse(items.fecha), it.nombre) }
        }
    }
}
