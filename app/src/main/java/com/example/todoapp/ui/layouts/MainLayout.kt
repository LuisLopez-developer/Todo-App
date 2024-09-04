package com.example.todoapp.ui.layouts

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
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.todoapp.Screen2
import com.example.todoapp.addtasks.ui.TaskViewModel
import com.example.todoapp.addtasks.ui.TasksScreen
import com.example.todoapp.taskcategory.ui.TaskCategoryScreen
import com.example.todoapp.taskcategory.ui.TaskCategoryViewModel
import com.example.todoapp.ui.navigation.Routes.Calendar
import com.example.todoapp.ui.navigation.Routes.Pantalla2
import com.example.todoapp.ui.navigation.Routes.TaskCategory

@Composable
fun MainLayout() {

    val taskViewModel: TaskViewModel = viewModel()
    val taskCategoryModel: TaskCategoryViewModel = viewModel()
    val navigationController = rememberNavController()

    Scaffold(
        topBar = { MyTopAppBar() },
        bottomBar = { BottomNavigationBar(navigationController) }
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
                    taskCategoryModel,
                    navigationController
                )
            }
            composable(Pantalla2.route) {
                Screen2()
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyTopAppBar() {
    CenterAlignedTopAppBar(colors = TopAppBarDefaults.topAppBarColors(
        containerColor = colorScheme.primaryContainer,
        titleContentColor = colorScheme.primary,
    ), title = { Text(text = "Todo App") })
}

@Composable
fun BottomNavigationBar(navigationController: NavController) {
    BottomAppBar {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = SpaceAround
        ) {
            IconButton(
                onClick = { navigationController.navigate(Calendar.route) }
            ) {
                Icon(
                    imageVector = Icons.Default.Call,
                    contentDescription = "Pantalla 1"
                )
            }

            IconButton(
                onClick = { navigationController.navigate(TaskCategory.route) }
            ) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Pantalla 2"
                )
            }

            IconButton(
                onClick = { navigationController.navigate(Pantalla2.route) }
            ) {
                Icon(
                    imageVector = Icons.Default.AddCircle,
                    contentDescription = "Pantalla 3"
                )
            }
        }
    }
}

