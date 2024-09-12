package com.example.todoapp.holidays.ui

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.todoapp.holidays.domain.GetHolidayByDateUseCase
import com.example.todoapp.holidays.domain.GetHolidaysUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.threeten.bp.LocalDate
import javax.inject.Inject

@HiltViewModel
class HolidaysViewModel @Inject constructor(
    private val getHolidaysUseCase: GetHolidaysUseCase,
    private val getHolidayByDateCaseUse: GetHolidayByDateUseCase,
) : ViewModel() {

    init {
        fetchHolidays()
    }

    private val _holidays = MutableStateFlow<List<LocalDate>>(emptyList())
    val holidays: StateFlow<List<LocalDate>> = _holidays

    // Obtener todos los días festivos y guardarlo en _holidays
    // Nota: actualmente getHolidaysUseCase su Date es un String con formato yyyy-MM-dd
    private fun fetchHolidays() {
        viewModelScope.launch {
            getHolidaysUseCase()?.map {
                LocalDate.parse(it.date) // Transformación a LocalDate
            }?.let { dates ->
                _holidays.value = dates
            } ?: run {
                Log.e("HolidaysViewModel", "No se encontró ninguna fecha")
            }
        }
    }

    // Obtener festividad por fecha específica
    private fun fetchHolidayByDate(fecha: String) {
        viewModelScope.launch {
            getHolidayByDateCaseUse(fecha)
        }
    }
}
