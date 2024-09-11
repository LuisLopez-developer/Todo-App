package com.example.todoapp.holidays.data.network

import com.example.todoapp.core.network.RetrofitHelper
import com.example.todoapp.holidays.data.network.response.HoliDaysResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class HoliDaysService {
    private val retrofit = RetrofitHelper.getRetrofit()

    suspend fun Holidays(): List<HoliDaysResponse>? {
        return withContext(Dispatchers.IO) {
            val response = retrofit.create(HoliDays::class.java).Holidays()
            if (response.isSuccessful) {
                response.body() // Devuelve la lista de HoliDaysResponse
            } else {
                null
            }
        }
    }

    suspend fun HolidayByDate(fecha: String): HoliDaysResponse? {
        return withContext(Dispatchers.IO) {
            val response = retrofit.create(HoliDays::class.java).HolidayByDate(fecha)
            if (response.isSuccessful) {
                response.body() // Devuelve un solo HoliDaysResponse
            } else {
                null // Manejo de errores
            }
        }
    }
}
