package com.example.todoapp.addtasks.ui

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@Composable
fun EditTaskScreen(taskViewModel: TaskViewModel, id: Int) {
    Text(text = "Pantalla de edición de tareas $id")
}