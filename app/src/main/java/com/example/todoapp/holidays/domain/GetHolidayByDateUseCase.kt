package com.example.todoapp.holidays.domain

import com.example.todoapp.holidays.data.HoliDaysRepository
import com.example.todoapp.holidays.ui.model.HolidaysModel
import javax.inject.Inject

class GetHolidayByDateUseCase @Inject constructor() {
    private val repository = HoliDaysRepository()

    suspend operator fun invoke(fecha: String): HolidaysModel? = repository.holidayByDate(fecha)

}