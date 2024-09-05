package com.example.todoapp.ui.navigation

data class ScreenConfig(
    val route: String,
    val hasTopBar: Boolean = true,
    val hasBottomBar: Boolean = true
)

object Routes {
    val Calendar = ScreenConfig("Calendar")
    val TaskCategory = ScreenConfig("TaskCategory")
    val EditTask = ScreenConfig("EditTask", hasTopBar = false, hasBottomBar = false)
    val Pantalla2 = ScreenConfig("Pantalla2")

    private val allRoutes = listOf(Calendar, TaskCategory, EditTask, Pantalla2)

    // Map para obtener rápidamente la configuración de cada ruta
    val routeMap = allRoutes.associateBy { it.route }
}