package com.example.todoapp.holidays.data

import com.example.todoapp.holidays.data.local.HolidayDao
import com.example.todoapp.holidays.data.local.HolidayEntity
import com.example.todoapp.holidays.data.network.HoliDaysService
import com.example.todoapp.holidays.domain.model.HolidayItem
import com.example.todoapp.holidays.domain.model.toDomain
import com.example.todoapp.holidays.domain.model.toDomainEntityList
import com.example.todoapp.holidays.domain.model.toDomainList
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HolidayRepository @Inject constructor(
    private val api: HoliDaysService,
    private val holidayDao: HolidayDao,
) {

    suspend fun getAllHolidaysFromApi(): List<HolidayItem> {
        return api.holidays().toDomainList()
    }

    suspend fun getHolidaysFromDatabase(): List<HolidayItem> {
        return holidayDao.getHolidays().toDomainEntityList()
    }

    suspend fun insertHolidays(holidays: List<HolidayEntity>) {
        holidayDao.addHolidays(holidays)
    }

    suspend fun holidayByDate(fecha: String): HolidayItem? {
        return api.holidayByDate(fecha)?.toDomain()
    }

    suspend fun clearHolidays() {
        holidayDao.deleteAllHolidays()
    }
}
