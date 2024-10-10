package com.example.todoapp.holidays.data.network

import com.example.todoapp.core.network.RetrofitHelper
import com.example.todoapp.holidays.data.network.response.HoliDaysResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Singleton

@Singleton
class HoliDaysService {
    private val retrofit = RetrofitHelper.getRetrofit()

    suspend fun holidays(): List<HoliDaysResponse> {
        return withContext(Dispatchers.IO) {
            val response = retrofit.create(HoliDays::class.java).holidays()
            response.body() ?: emptyList()
        }
    }

    suspend fun holidayByDate(fecha: String): HoliDaysResponse? {
        return withContext(Dispatchers.IO) {
            val response = retrofit.create(HoliDays::class.java).holidayByDate(fecha)
            response.body()
        }
    }
}
