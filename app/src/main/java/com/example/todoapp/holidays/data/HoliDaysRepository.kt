package com.example.todoapp.holidays.data

import android.content.Context
import com.example.todoapp.holidays.data.local.HolidayDao
import com.example.todoapp.holidays.data.local.HolidayEntity
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
    private val holidayDao: HolidayDao,
) {

    private suspend fun addHolidays(holidays: List<HolidayModel>) {
        val existingHolidays = holidayDao.getHolidays()
        val newHolidays = holidays.filter { holiday ->
            existingHolidays.none { it.date == holiday.date && it.name == holiday.name }
        }
        holidayDao.addHolidays(newHolidays.map {
            HolidayEntity(date = it.date, name = it.name)
        })
    }

    // Obtener todas las festividades
    suspend fun holidays(): List<HolidayModel>? {
        val holiday: List<HolidayModel>?

        if (NetworkUtils.isInternetAvailable(context)) {
            holiday = api.holidays()?.map { items ->
                HolidayModel(LocalDate.parse(items.fecha), items.nombre)
            }

            holiday?.let { items ->
                addHolidays(items)
            }
        }else{
            holiday = holidayDao.getHolidays().map { items ->
                HolidayModel(items.date, items.name)
            }
        }

        return holiday
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
