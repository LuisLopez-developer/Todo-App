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

    private val _holidays = MutableStateFlow<List<HolidayModel>>(emptyList())
    val holidays: StateFlow<List<HolidayModel>> get() = _holidays

    init {
        fetchHolidays()
    }

    // Obtener todos los días festivos y guardarlo en _holidays
    private fun fetchHolidays() {
        viewModelScope.launch {
            try {
                val result = getHolidaysUseCase()
                if (result != null) {
                    if (result.isEmpty()) {
                        Log.e(
                            "HolidaysViewModel",
                            "No hay festividades"
                        )
                    }
                    _holidays.value = result
                } else {
                    Log.e(
                        "HolidaysViewModel",
                        "Sin conexión a internet"
                    )
                    _holidays.value = emptyList()
                }
            } catch (e: Exception) {
                Log.e("HolidaysViewModel", "Error en fetchHolidays: ${e.message}")
                _holidays.value = emptyList()
            }
        }
    }

    // Obtener festividad por fecha específica
    private fun fetchHolidayByDate(fecha: String) {
        viewModelScope.launch {
            try {
                val result = getHolidayByDateCaseUse(fecha)
                // Manejo del resultado si es necesario
            } catch (e: Exception) {
                Log.e("HolidaysViewModel", "Error en fetchHolidayByDate: ${e.message}")
            }
        }
    }
}