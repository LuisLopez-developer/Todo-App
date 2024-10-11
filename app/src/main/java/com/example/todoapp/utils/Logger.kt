package com.example.todoapp.utils

import android.util.Log
import com.example.todoapp.BuildConfig

object Logger {
    fun debug(tag: String, message: String) {
        // Verifica si la app está en modo debug y no se está ejecutando una prueba
        if (BuildConfig.DEBUG && isNotTest()) {
            Log.d(tag, message)
        }
    }

    // Verifica si no se está ejecutando una prueba
    private fun isNotTest(): Boolean {
        // Verifica si la clase org.junit.Test está disponible
        return try {
            Class.forName("org.junit.Test")
            false
        } catch (e: ClassNotFoundException) {
            true
        }
    }

    fun error(tag: String, message: String, throwable: Throwable? = null) {
        Log.e(tag, message, throwable)
    }

    fun info(tag: String, message: String) {
        Log.i(tag, message)
    }

    // Puedes agregar más métodos si es necesario (info, warning, etc.)
}