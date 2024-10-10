package com.example.todoapp.holidays.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.todoapp.holidays.domain.GetHolidayByDateUseCase
import com.example.todoapp.holidays.domain.GetHolidaysUseCase
import com.example.todoapp.holidays.ui.model.HolidayModel
import com.example.todoapp.holidays.ui.model.toViewModelList
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
            _holidays.value = getHolidaysUseCase().toViewModelList()
        }
    }

    // Obtener festividad por fecha específica
    private fun fetchHolidayByDate(fecha: String) {
        viewModelScope.launch {
            val result = getHolidayByDateCaseUse(fecha)
            result?.let {
                _holidays.value = listOf(it).toViewModelList()
            }
        }
    }
}