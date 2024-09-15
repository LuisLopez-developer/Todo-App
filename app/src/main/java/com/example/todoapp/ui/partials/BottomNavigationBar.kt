package com.example.todoapp.ui.partials

import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.todoapp.R.drawable
import com.example.todoapp.R.string
import com.example.todoapp.ui.navigation.CalendarRoute
import com.example.todoapp.ui.navigation.SettingsRoute
import com.example.todoapp.ui.navigation.TaskCategoryRoute
import com.example.todoapp.ui.navigation.TaskListRoute
import kotlinx.serialization.ExperimentalSerializationApi

@Composable
fun BottomNavigationBar(navigationController: NavController) {
    val currentDestination = navigationController.currentBackStackEntryAsState().value?.destination
    NavigationBar {
        bottomNavigationItems.forEach { item ->
            NavigationBarItem(
                selected = currentDestination?.route == item.route,
                onClick = {
                    navigationController.navigateTo(item.route)
                },
                icon = {
                    Icon(
                        painter = painterResource(item.icon),
                        contentDescription = stringResource(item.contentDescription)
                    )
                },
                label = { Text(text = stringResource(item.label)) }
            )
        }
    }
}

fun NavController.navigateTo(route: String) {
    navigate(route) {
        popUpTo(graph.startDestinationId) {
            saveState = true
        }
        launchSingleTop = true
        restoreState = true
    }
}

data class BottomNavigationItem(
    val route: String,
    val icon: Int,
    val contentDescription: Int,
    val label: Int,
)

@OptIn(ExperimentalSerializationApi::class)
val bottomNavigationItems = listOf(
    BottomNavigationItem(
        CalendarRoute.serializer().descriptor.serialName,
        drawable.ic_calendar,
        string.calendar_content_description,
        string.bb_calendar_title
    ),
    BottomNavigationItem(
        TaskListRoute.serializer().descriptor.serialName,
        drawable.ic_task,
        string.task_list_content_description,
        string.bb_task_list_title
    ),
    BottomNavigationItem(
        TaskCategoryRoute.serializer().descriptor.serialName,
        drawable.ic_notes,
        string.task_category_content_description,
        string.bb_task_category_title
    ),
    BottomNavigationItem(
        SettingsRoute.serializer().descriptor.serialName,
        drawable.ic_settings,
        string.settings_content_description,
        string.bb_settings
    )
)