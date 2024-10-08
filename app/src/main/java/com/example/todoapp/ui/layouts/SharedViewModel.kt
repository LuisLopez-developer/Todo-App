package com.example.todoapp.ui.layouts

import androidx.compose.foundation.layout.RowScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel

class SharedViewModel : ViewModel() {
    val topBarTitle = mutableStateOf("Todo App")
    val topBarActions = mutableStateOf<@Composable RowScope.() -> Unit>({})
    val topBarNavigationIcon = mutableStateOf<@Composable () -> Unit>({})
}