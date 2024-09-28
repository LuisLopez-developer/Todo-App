package com.example.todoapp.addtasks.ui.editTask

import com.example.todoapp.addtasks.ui.model.TaskModel

sealed interface TaskUiState {
    data object Loading: TaskUiState
    data class Error(val throwable: Throwable): TaskUiState
    data class Success(val task: TaskModel): TaskUiState
    data object Empty: TaskUiState // Estado para cuando no hay tarea disponible (por ejemplo, ID no encontrado)
}