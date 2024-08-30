package com.example.todoapp.navigation

sealed class Routes(val route: String) {
    data object Calendar : Routes("Calendar")
    data object Pantalla1 : Routes("Pantalla1")
    data object Pantalla2 : Routes("Pantalla2")
    data object Pantalla3 : Routes("Pantalla3/{id}") {
        fun createRoute(id: Int) = "pantalla3/$id"
    }
}