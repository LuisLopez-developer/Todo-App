package com.example.todoapp.ui.navigation

import kotlinx.serialization.Serializable

@Serializable
object CalendarRoute

@Serializable
object TaskCategoryRoute

@Serializable
object SettingsRoute

@Serializable
object TaskListRoute

@Serializable
data class EditTaskRoute(val id: Int)