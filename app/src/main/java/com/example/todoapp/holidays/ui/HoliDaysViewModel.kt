package com.example.todoapp.holidays.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.todoapp.holidays.domain.HoliDaysUseCase
import kotlinx.coroutines.launch

class HoliDaysViewModel : ViewModel() {

    private val useCase = HoliDaysUseCase()

    // LiveData para obtener todos los días festivos
    private val _holidays = MutableLiveData<String>()
    val holidays: LiveData<String> get() = _holidays

    // LiveData para obtener festividades por fecha específica
    private val _holidayByDate = MutableLiveData<String>()
    val holidayByDate: LiveData<String> get() = _holidayByDate

    // Obtener todos los días festivos
    fun fetchHolidays() {
        viewModelScope.launch {
            val result = useCase.invoke()
            _holidays.postValue(result)
        }
    }

    // Obtener festividad por fecha específica
    fun fetchHolidayByDate(fecha: String) {
        viewModelScope.launch {
            val result = useCase.HolidayByDate(fecha)
            _holidayByDate.postValue(result)
        }
    }
}
