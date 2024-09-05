package com.example.todoapp.ui.layouts

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.todoapp.Screen2
import com.example.todoapp.addtasks.ui.EditTaskScreen
import com.example.todoapp.addtasks.ui.TaskViewModel
import com.example.todoapp.addtasks.ui.TasksScreen
import com.example.todoapp.taskcategory.ui.TaskCategoryScreen
import com.example.todoapp.taskcategory.ui.TaskCategoryViewModel
import com.example.todoapp.ui.navigation.Routes.Calendar
import com.example.todoapp.ui.navigation.Routes.EditTask
import com.example.todoapp.ui.navigation.Routes.Pantalla2
import com.example.todoapp.ui.navigation.Routes.TaskCategory
import com.example.todoapp.ui.navigation.Routes.routeMap
import com.example.todoapp.ui.partials.BottomNavigationBar
import com.example.todoapp.ui.partials.TopAppBar

@Composable
fun MainLayout() {
    val taskViewModel: TaskViewModel = viewModel()
    val taskCategoryViewModel: TaskCategoryViewModel = viewModel()
    val navigationController = rememberNavController()

    // Obtener la ruta actual
    val currentBackStackEntry = navigationController.currentBackStackEntryAsState()
    val currentRoute = currentBackStackEntry.value?.destination?.route

    // Obtener la configuración de la ruta actual desde el Map
    val currentScreenConfig = routeMap[currentRoute] ?: Calendar

    Scaffold(
        topBar = {
            if (currentScreenConfig.hasTopBar) {
                TopAppBar()
            }
        },
        bottomBar = {
            if (currentScreenConfig.hasBottomBar) {
                BottomNavigationBar(navigationController)
            }
        }
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
            composable(TaskCategory.route) {
                TaskCategoryScreen(
                    taskCategoryViewModel,
                    navigationController
                )
            }
            composable(EditTask.route) {
                EditTaskScreen(taskCategoryViewModel)
            }
            composable(Pantalla2.route) {
                Screen2()
            }
        }
    }
}