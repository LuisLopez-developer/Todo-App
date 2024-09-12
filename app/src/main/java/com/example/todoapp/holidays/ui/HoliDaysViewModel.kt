package com.example.todoapp.holidays.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.todoapp.holidays.domain.GetHolidayByDateUseCase
import com.example.todoapp.holidays.domain.GetHolidaysUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HolidaysViewModel @Inject constructor(
    private val getHolidaysUseCase: GetHolidaysUseCase,
    private val getHolidayByDateCaseUse: GetHolidayByDateUseCase,
) : ViewModel() {

    init {
        fetchHolidays()
    }

    // Obtener todos los días festivos
    private fun fetchHolidays() {
        viewModelScope.launch {
            getHolidaysUseCase()?.map {
                it.date
            }
        }
    }

    // Obtener festividad por fecha específica
    fun fetchHolidayByDate(fecha: String) {
        viewModelScope.launch {
            getHolidayByDateCaseUse(fecha)
        }
    }
}
