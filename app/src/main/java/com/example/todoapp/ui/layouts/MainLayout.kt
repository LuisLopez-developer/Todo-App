package com.example.todoapp.ui.layouts

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.todoapp.Screen2
import com.example.todoapp.addtasks.ui.EditTaskScreen
import com.example.todoapp.addtasks.ui.TaskViewModel
import com.example.todoapp.addtasks.ui.TasksScreen
import com.example.todoapp.taskcategory.ui.TaskCategoryScreen
import com.example.todoapp.taskcategory.ui.TaskCategoryViewModel
import com.example.todoapp.ui.navigation.CalendarRoute
import com.example.todoapp.ui.navigation.EditTaskRoute
import com.example.todoapp.ui.navigation.Pantalla2Route
import com.example.todoapp.ui.navigation.TaskCategoryRoute
import com.example.todoapp.ui.partials.BottomNavigationBar
import com.example.todoapp.ui.partials.TopAppBar

@Composable
fun MainLayout() {
    val taskViewModel: TaskViewModel = viewModel()
    val taskCategoryViewModel: TaskCategoryViewModel = viewModel()
    val navigationController = rememberNavController()

    Scaffold(
        topBar = {

            TopAppBar()

        },
        bottomBar = {

            BottomNavigationBar(navigationController)

        }
    ) { innerPadding ->
        NavHost(
            navController = navigationController,
            startDestination = CalendarRoute,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable<CalendarRoute> {
                TasksScreen(
                    taskViewModel,
                    navigationController
                )
            }
            composable<TaskCategoryRoute> {
                TaskCategoryScreen(
                    taskCategoryViewModel,
                    navigationController
                )
            }
            composable<EditTaskRoute> {
                val args = it.toRoute<EditTaskRoute>()
                EditTaskScreen(taskViewModel, args.id)
            }
            composable<Pantalla2Route> {
                Screen2()
            }
        }
    }
}