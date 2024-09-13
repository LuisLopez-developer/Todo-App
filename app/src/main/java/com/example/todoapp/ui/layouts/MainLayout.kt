package com.example.todoapp.ui.layouts

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.todoapp.Screen2
import com.example.todoapp.addtasks.ui.EditTaskScreen
import com.example.todoapp.addtasks.ui.TaskViewModel
import com.example.todoapp.addtasks.ui.TasksScreen
import com.example.todoapp.holidays.ui.HolidaysViewModel
import com.example.todoapp.taskcategory.ui.TaskCategoryScreen
import com.example.todoapp.taskcategory.ui.TaskCategoryViewModel
import com.example.todoapp.ui.constants.StylesTopBar
import com.example.todoapp.ui.navigation.CalendarRoute
import com.example.todoapp.ui.navigation.EditTaskRoute
import com.example.todoapp.ui.navigation.Pantalla2Route
import com.example.todoapp.ui.navigation.TaskCategoryRoute
import com.example.todoapp.ui.partials.BottomNavigationBar
import com.example.todoapp.ui.partials.TopAppBar
import com.example.todoapp.ui.partials.TopAppBarSecondary
import com.example.todoapp.ui.utils.extractCleanRoute

@Composable
fun MainLayout() {
    val taskViewModel: TaskViewModel = viewModel()
    val taskCategoryViewModel: TaskCategoryViewModel = viewModel()
    val holidaysModel: HolidaysViewModel = viewModel()
    val navigationController = rememberNavController()

    val bottomBarState = rememberSaveable { mutableStateOf(true) }
    val styleTopBarState = rememberSaveable { mutableStateOf(StylesTopBar.MAIN) }

    val navBackStackEntry by navigationController.currentBackStackEntryAsState()

    //Agregar rutas que no necesiten el bottomBar
    LaunchedEffect(navBackStackEntry) {
        val currentRoute = navBackStackEntry?.destination?.route

        val cleanedRoute = extractCleanRoute(currentRoute ?: "")
        when (cleanedRoute) {
            extractCleanRoute(EditTaskRoute.toString()) -> {
                bottomBarState.value = false
                styleTopBarState.value = StylesTopBar.SECONDARY_BACK_OPTIONS
            }
            else -> {
                bottomBarState.value = true
                styleTopBarState.value = StylesTopBar.MAIN
            }
        }
    }

    Scaffold(
        topBar = {
            if (styleTopBarState.value == StylesTopBar.MAIN) {
                TopAppBar()
            } else {
                TopAppBarSecondary(
                    style = styleTopBarState.value,
                    navController = navigationController
                )
            }
        },
        bottomBar = {
            if (bottomBarState.value) {
                BottomNavigationBar(navigationController)
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navigationController,
            startDestination = CalendarRoute,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable<CalendarRoute> {
                TasksScreen(
                    taskViewModel = taskViewModel,
                    taskCategoryViewModel = taskCategoryViewModel,
                    holidaysViewModel = holidaysModel,
                    navigationController = navigationController
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
                EditTaskScreen(taskViewModel,taskCategoryViewModel, args.id)
            }
            composable<Pantalla2Route> {
                Screen2()
            }
        }
    }
}