package com.example.todoapp.holidays.data.network

import com.example.todoapp.holidays.data.network.response.HoliDaysResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface HoliDays {
    // Método existente para obtener todas las festividades
    @GET("/festividades")
    suspend fun Holidays(): Response<List<HoliDaysResponse>>

    // Nuevo método para obtener las festividades de una fecha específica
    @GET("/festividad")
    suspend fun HolidayByDate(@Query("fecha") fecha: String): Response<HoliDaysResponse>
}
