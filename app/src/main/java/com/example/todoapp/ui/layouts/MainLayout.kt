package com.example.todoapp.ui.layouts

import android.util.Log
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
import com.example.todoapp.addtasks.ui.TaskViewModel
import com.example.todoapp.addtasks.ui.TasksScreen
import com.example.todoapp.addtasks.ui.editTask.EditTaskScreen
import com.example.todoapp.addtasks.ui.editTask.TaskEditViewModel
import com.example.todoapp.addtasks.ui.taskList.TaskListScreen
import com.example.todoapp.addtasks.ui.taskList.TaskListViewModel
import com.example.todoapp.holidays.ui.HolidaysViewModel
import com.example.todoapp.services.notification.RequestNotificationPermission
import com.example.todoapp.settings.drive.ui.DriveScreen
import com.example.todoapp.settings.drive.ui.DriveViewModel
import com.example.todoapp.settings.ui.SettingsScreen
import com.example.todoapp.settings.ui.SettingsViewModel
import com.example.todoapp.settings.utils.toJson
import com.example.todoapp.taskcategory.ui.TaskCategoryScreen
import com.example.todoapp.taskcategory.ui.TaskCategoryViewModel
import com.example.todoapp.ui.constants.StylesTopBar
import com.example.todoapp.ui.navigation.CalendarRoute
import com.example.todoapp.ui.navigation.DriveRoute
import com.example.todoapp.ui.navigation.EditTaskRoute
import com.example.todoapp.ui.navigation.SettingsRoute
import com.example.todoapp.ui.navigation.TaskCategoryRoute
import com.example.todoapp.ui.navigation.TaskListRoute
import com.example.todoapp.ui.partials.BottomNavigationBar
import com.example.todoapp.ui.partials.TopAppBar
import com.example.todoapp.ui.utils.extractCleanRoute

@Composable
fun MainLayout(permissionService: RequestNotificationPermission) {
    val taskViewModel: TaskViewModel = viewModel()
    val taskCategoryViewModel: TaskCategoryViewModel = viewModel()
    val taskListViewModel: TaskListViewModel = viewModel()
    val holidaysModel: HolidaysViewModel = viewModel()
    val settingsViewModel: SettingsViewModel = viewModel()
    val taskEditViewModel: TaskEditViewModel = viewModel()
    val driveViewModel: DriveViewModel = viewModel()

    val sharedViewModel: SharedViewModel = viewModel()

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
                styleTopBarState.value = StylesTopBar.MAIN
            }

            extractCleanRoute(DriveRoute.toString()) -> {
                bottomBarState.value = false
                styleTopBarState.value = StylesTopBar.MAIN
            }

            else -> {
                bottomBarState.value = true
                styleTopBarState.value = StylesTopBar.MAIN
                // Restablecer valores del TopAppBar
                sharedViewModel.topBarTitle.value = "Todo App"
                sharedViewModel.topBarNavigationIcon.value = {}
                sharedViewModel.topBarActions.value = {}
            }
        }
    }

    Scaffold(
        topBar = {
            if (styleTopBarState.value == StylesTopBar.MAIN) {
                TopAppBar(
                    title = sharedViewModel.topBarTitle.value,
                    navigationIcon = sharedViewModel.topBarNavigationIcon.value,
                    actions = sharedViewModel.topBarActions.value
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
                    navigationController = navigationController,
                    permissionService = permissionService
                )
            }
            composable<TaskCategoryRoute> {
                TaskCategoryScreen(
                    taskCategoryViewModel
                )
            }
            composable<EditTaskRoute> {
                val args = it.toRoute<EditTaskRoute>()
                EditTaskScreen(
                    taskCategoryViewModel = taskCategoryViewModel,
                    taskEditViewModel = taskEditViewModel,
                    id = args.id,
                    sharedViewModel = sharedViewModel,
                    navController = navigationController
                )
            }
            composable<SettingsRoute> {
                SettingsScreen(
                    settingsViewModel = settingsViewModel,
                    navController = navigationController
                )
            }
            composable<TaskListRoute> {
                TaskListScreen(
                    taskListViewModel = taskListViewModel,
                    taskCategoryViewModel = taskCategoryViewModel,
                    navigationController = navigationController
                )
            }
            composable<DriveRoute> {
                val args = it.toRoute<DriveRoute>()
                DriveScreen(
                    userId = args.userId,
                    driveViewModel = driveViewModel,
                    sharedViewModel = sharedViewModel,
                    navController = navigationController
                )
            }
        }
    }
}