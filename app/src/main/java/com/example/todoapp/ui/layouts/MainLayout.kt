package com.example.todoapp.ui.layouts

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement.SpaceAround
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.todoapp.Screen1
import com.example.todoapp.Screen2
import com.example.todoapp.Screen3
import com.example.todoapp.addtasks.ui.TaskViewModel
import com.example.todoapp.addtasks.ui.TasksScreen
import com.example.todoapp.navigation.Routes.Calendar
import com.example.todoapp.navigation.Routes.Pantalla1
import com.example.todoapp.navigation.Routes.Pantalla2
import com.example.todoapp.navigation.Routes.Pantalla3

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MainLayout() {

    val taskViewModel: TaskViewModel = viewModel()
    val navigationController = rememberNavController()

    Scaffold(
        topBar = { MyTopAppBar() },
        bottomBar = { BottomNavigationBar(navigationController) } // Utilizamos la barra de navegación inferior
    ) { innerPadding ->
        NavHost(
            navController = navigationController,
            startDestination = Calendar.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Calendar.route) {
                TasksScreen(
                    taskViewModel,
                    navigationController
                )
            }
            composable(Pantalla1.route) { Screen1(navigationController) }
            composable(Pantalla2.route) { Screen2(navigationController) }
            composable(
                Pantalla3.route,
                arguments = listOf(navArgument("id") { type = NavType.IntType })
            ) { backStackEntry ->
                Screen3(
                    navigationController,
                    backStackEntry.arguments?.getInt("id") ?: 0
                )
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyTopAppBar() {
    CenterAlignedTopAppBar(colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
        containerColor = MaterialTheme.colorScheme.primaryContainer,
        titleContentColor = MaterialTheme.colorScheme.primary,
    ), title = { Text(text = "Todo App") })
}

@Composable
fun BottomNavigationBar(navigationController: NavController) {
    BottomAppBar(
        containerColor = MaterialTheme.colorScheme.primaryContainer,
        contentColor = MaterialTheme.colorScheme.onPrimaryContainer
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = SpaceAround
        ) {
            IconButton(
                onClick = { navigationController.navigate(Pantalla1.route) }
            ) {
                Icon(
                    imageVector = Icons.Default.Call,
                    contentDescription = "Pantalla 1"
                )
            }

            IconButton(
                onClick = { navigationController.navigate(Pantalla2.route) }
            ) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Pantalla 2"
                )
            }

            IconButton(
                onClick = { navigationController.navigate(Pantalla3.createRoute(2)) }
            ) {
                Icon(
                    imageVector = Icons.Default.AddCircle,
                    contentDescription = "Pantalla 3"
                )
            }
        }
    }
}

