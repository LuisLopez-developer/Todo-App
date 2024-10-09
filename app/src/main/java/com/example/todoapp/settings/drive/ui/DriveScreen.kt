package com.example.todoapp.settings.drive.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavHostController
import com.example.todoapp.ui.layouts.SharedViewModel

@Composable
fun DriveScreen(
    userId: String,
    driveViewModel: DriveViewModel,
    sharedViewModel: SharedViewModel,
    navController: NavHostController
) {
    // Establecer el ID de la tarea en el ViewModel
    LaunchedEffect(userId) {
        driveViewModel.setUserId(userId)
    }

}

@Composable
fun Content() {

}

@Composable
fun ConfigTopBar() {

}

@Composable
fun Container(){

}