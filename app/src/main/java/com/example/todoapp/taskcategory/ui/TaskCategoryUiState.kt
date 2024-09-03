package com.example.todoapp.taskcategory.ui

import com.example.todoapp.taskcategory.ui.model.TaskCategoryModel

sealed interface TaskCategoryUiState {
    data object Loading: TaskCategoryUiState
    data class Error(val throwable: Throwable): TaskCategoryUiState
    data class Success(val categories: List<TaskCategoryModel>): TaskCategoryUiState
}