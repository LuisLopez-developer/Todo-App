package com.example.todoapp.settings.utils

import com.google.gson.Gson

// Extencion de la clase Any para convertir a JSON
fun Any.toJson(): String {
    return Gson().toJson(this)
}