package com.example.todoapp.holidays.domain

import com.example.todoapp.core.NetWorkService
import com.example.todoapp.holidays.data.HolidayRepository
import com.example.todoapp.holidays.data.local.toDatabaseList
import com.example.todoapp.holidays.domain.model.HolidayItem
import javax.inject.Inject

class GetHolidaysUseCase @Inject constructor(
    private val repository: HolidayRepository,
    private val netWorkService: NetWorkService,
) {
    suspend operator fun invoke(): List<HolidayItem> {
        if (!netWorkService.getNetworkService(showErrorToast = false)) {
            return repository.getHolidaysFromDatabase()
        }

        val holidays = repository.getAllHolidaysFromApi()

        if (holidays.isEmpty()) {
            return repository.getHolidaysFromDatabase()
        }

        repository.clearHolidays()
        repository.insertHolidays(holidays.toDatabaseList())

        return holidays
    }
}