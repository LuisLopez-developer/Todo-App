package com.example.todoapp.holidays.data.network

import com.example.todoapp.core.network.RetrofitHelper
import com.example.todoapp.holidays.data.network.response.holiDaysResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class HoliDaysService {
    private val retrofit = RetrofitHelper.getRetrofit()

    suspend fun holidays(): List<holiDaysResponse>? {
        return withContext(Dispatchers.IO) {
            val response = retrofit.create(HoliDays::class.java).holidays()
            if (response.isSuccessful) {
                response.body() // Devuelve la lista de HoliDaysResponse
            } else {
                null
            }
        }
    }

    suspend fun holidayByDate(fecha: String): holiDaysResponse? {
        return withContext(Dispatchers.IO) {
            val response = retrofit.create(HoliDays::class.java).holidayByDate(fecha)
            if (response.isSuccessful) {
                response.body() // Devuelve un solo HoliDaysResponse
            } else {
                null // Manejo de errores
            }
        }
    }
}
