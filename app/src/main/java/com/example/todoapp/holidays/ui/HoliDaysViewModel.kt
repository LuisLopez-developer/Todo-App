package com.example.todoapp.holidays.ui

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.todoapp.holidays.domain.GetHolidayByDateUseCase
import com.example.todoapp.holidays.domain.GetHolidaysUseCase
import com.example.todoapp.holidays.ui.model.HolidayModel
import com.example.todoapp.holidays.utils.NetworkUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HolidaysViewModel @Inject constructor(
    private val getHolidaysUseCase: GetHolidaysUseCase,
    private val getHolidayByDateCaseUse: GetHolidayByDateUseCase,
    @ApplicationContext private val context: Context // Inyecta el contexto de la aplicación
) : ViewModel() {

    init {
        fetchHolidays()
    }

    private val _holidays = MutableStateFlow<List<HolidayModel>>(emptyList())
    val holidays: StateFlow<List<HolidayModel>> = _holidays

    // Obtener todos los días festivos y guardarlo en _holidays
    private fun fetchHolidays() {
        viewModelScope.launch {
            if (NetworkUtils.isInternetAvailable(context)) { // Verifica si hay conexión antes de llamar a la API
                getHolidaysUseCase()?.let { holidays ->
                    _holidays.value = holidays.map { item ->
                        HolidayModel(item.date, item.name)
                    }
                } ?: Log.e("HolidaysViewModel", "No se encontraron días festivos")
            } else {
                Log.e("HolidaysViewModel", "Sin conexión a internet")
                _holidays.value = emptyList() // Devuelve lista vacía si no hay conexión
            }
        }
    }

    // Obtener festividad por fecha específica
    private fun fetchHolidayByDate(fecha: String) {
        viewModelScope.launch {
            if (NetworkUtils.isInternetAvailable(context)) {
                getHolidayByDateCaseUse(fecha)?.let {
                    // Aquí puedes manejar el resultado si necesitas.
                }
            } else {
                Log.e("HolidaysViewModel", "Sin conexión a internet")
                // Manejo cuando no hay conexión, por ejemplo, no hacer nada o devolver un mensaje al usuario
            }
        }
    }
}