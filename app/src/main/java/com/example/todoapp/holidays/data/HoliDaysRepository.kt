package com.example.todoapp.holidays.data

import android.content.Context
import com.example.todoapp.holidays.data.network.HoliDaysService
import com.example.todoapp.holidays.ui.model.HolidayModel
import com.example.todoapp.holidays.utils.NetworkUtils
import dagger.hilt.android.qualifiers.ApplicationContext
import org.threeten.bp.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HoliDaysRepository @Inject constructor(
    @ApplicationContext private val context: Context,
    private val api: HoliDaysService,
) {

    // Obtener todas las festividades
    suspend fun holidays(): List<HolidayModel>? {
        return if (NetworkUtils.isInternetAvailable(context)) {
            api.holidays()?.map { items ->
                HolidayModel(LocalDate.parse(items.fecha), items.nombre)
            }
        } else {
            null
        }
    }

    // Obtener festividad por fecha específica
    suspend fun holidayByDate(fecha: String): HolidayModel? {
        return if (NetworkUtils.isInternetAvailable(context)) {
            api.holidayByDate(fecha)?.let { items ->
                HolidayModel(LocalDate.parse(items.fecha), items.nombre)
            }
        } else {
            null
        }
    }
}
