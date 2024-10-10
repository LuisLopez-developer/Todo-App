package com.example.todoapp.holidays.domain

import com.example.todoapp.core.NetWorkService
import com.example.todoapp.holidays.data.HolidayRepository
import com.example.todoapp.holidays.domain.model.HolidayItem
import javax.inject.Inject

class GetHolidayByDateUseCase @Inject constructor(
    private val repository: HolidayRepository,
    private val netWorkService: NetWorkService,
) {
    suspend operator fun invoke(fecha: String): HolidayItem? {
        if (!netWorkService.getNetworkService(showErrorToast = false)) {
            return null
        }

        return repository.holidayByDate(fecha)
    }
}