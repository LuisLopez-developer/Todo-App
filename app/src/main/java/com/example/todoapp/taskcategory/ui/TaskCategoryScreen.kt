package com.example.todoapp.taskcategory.ui

import androidx.compose.runtime.Composable
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.NavHostController

@Composable
fun TaskCategoryScreen(
    taskCategoryModel: TaskCategoryViewModel,
    navigationController: NavHostController,
) {
    val lifecycle = LocalLifecycleOwner.current.lifecycle


}