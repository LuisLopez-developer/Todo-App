package com.example.todoapp.holidays.data.network.response

import com.google.gson.annotations.SerializedName

data class holiDaysResponse(
    @SerializedName("fecha") val fecha: String, // Representa la fecha en formato String
    @SerializedName("nombre") val nombre: String // Representa el nombre del día festivo
)