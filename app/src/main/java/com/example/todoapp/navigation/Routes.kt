package com.example.todoapp.navigation

sealed class Routes(val route: String) {
    data object Calendar : Routes("Calendar")
    data object TaskCategory : Routes("TaskCategory")
    data object Pantalla2 : Routes("Pantalla2")
}