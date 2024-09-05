package com.example.todoapp.ui.partials

import androidx.compose.foundation.layout.Arrangement.SpaceAround
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.example.todoapp.ui.navigation.CalendarRoute
import com.example.todoapp.ui.navigation.Pantalla2Route
import com.example.todoapp.ui.navigation.TaskCategoryRoute


@Composable
fun BottomNavigationBar(navigationController: NavController) {
    BottomAppBar {
        Row(
            modifier = Modifier.fillMaxWidth(), horizontalArrangement = SpaceAround
        ) {
            IconButton(onClick = {
                navigationController.navigate(CalendarRoute) {
                    popUpTo(navigationController.graph.startDestinationId) {
                        saveState = true
                    }
                    launchSingleTop = true
                    restoreState = true
                }
            }) {
                Icon(
                    imageVector = Icons.Default.Call, contentDescription = "Pantalla 1"
                )
            }

            IconButton(onClick = {
                navigationController.navigate(TaskCategoryRoute) {
                    popUpTo(navigationController.graph.startDestinationId) {
                        saveState = true
                    }
                    launchSingleTop = true
                    restoreState = true
                }
            }) {
                Icon(
                    imageVector = Icons.Default.Check, contentDescription = "Pantalla 2"
                )
            }

            IconButton(onClick = {
                navigationController.navigate(Pantalla2Route) {
                    popUpTo(navigationController.graph.startDestinationId) {
                        saveState = true
                    }
                    launchSingleTop = true
                    restoreState = true
                }
            }) {
                Icon(
                    imageVector = Icons.Default.AddCircle, contentDescription = "Pantalla 3"
                )
            }
        }
    }
}