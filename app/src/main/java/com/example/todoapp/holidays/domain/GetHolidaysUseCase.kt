package com.example.todoapp.holidays.domain

import com.example.todoapp.holidays.data.HoliDaysRepository
import com.example.todoapp.holidays.ui.model.HolidayModel
import javax.inject.Inject

class GetHolidaysUseCase @Inject constructor() {
    private val repository = HoliDaysRepository()

    suspend operator fun invoke(): List<HolidayModel>? = repository.holidays()
}