package com.example.todoapp.holidays.ui

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.todoapp.holidays.domain.GetHolidayByDateUseCase
import com.example.todoapp.holidays.domain.GetHolidaysUseCase
import com.example.todoapp.holidays.ui.model.HolidayModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
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

    private val _holidays = MutableStateFlow<List<HolidayModel>>(emptyList())
    val holidays: StateFlow<List<HolidayModel>> = _holidays

    // Obtener todos los días festivos y guardarlo en _holidays
    // Nota: actualmente getHolidaysUseCase su Date es un String con formato yyyy-MM-dd
    private fun fetchHolidays() {
        viewModelScope.launch {
            // Ejecutará el bloque solo si getHolidaysUseCase() no es null
            getHolidaysUseCase()?.let { holidays ->
                // Mapea la lista de respuestas a una lista de HolidayModel
                _holidays.value = holidays.map { item ->
                    HolidayModel(item.date, item.name)
                }
            } ?: Log.e("HolidaysViewModel", "No se encontraron días festivos")
        }
    }


    // Obtener festividad por fecha específica
    private fun fetchHolidayByDate(fecha: String) {
        viewModelScope.launch {
            getHolidayByDateCaseUse(fecha)
        }
    }
}
