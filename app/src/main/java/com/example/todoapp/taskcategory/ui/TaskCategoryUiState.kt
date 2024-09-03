package com.example.todoapp.taskcategory.ui

import com.example.todoapp.addtasks.ui.model.TaskModel

sealed interface TaskCategoryUiState {
    data object Loading: TaskCategoryUiState
    data class Error(val throwable: Throwable): TaskCategoryUiState
    data class Success(val tasks:List<TaskModel>): TaskCategoryUiState
}