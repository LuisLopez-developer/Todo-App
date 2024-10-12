package com.example.todoapp.addtasks.ui

import android.content.Context
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.todoapp.R.drawable
import com.example.todoapp.R.string
import com.example.todoapp.R.string.ic_calendar
import com.example.todoapp.addtasks.ui.components.BottomSheetComponent
import com.example.todoapp.addtasks.ui.components.TaskItemComponent
import com.example.todoapp.addtasks.ui.model.TaskModel
import com.example.todoapp.holidays.ui.HolidaysViewModel
import com.example.todoapp.holidays.ui.model.HolidayModel
import com.example.todoapp.state.data.constants.DefaultStateId.DELETED_ID
import com.example.todoapp.taskcategory.ui.TaskCategoryUiState
import com.example.todoapp.taskcategory.ui.TaskCategoryViewModel
import com.example.todoapp.taskcategory.ui.model.TaskCategoryModel
import com.example.todoapp.ui.components.CalendarComponent
import com.example.todoapp.ui.navigation.EditTaskRoute
import com.example.todoapp.utils.Logger
import org.threeten.bp.LocalDate

@Composable
fun TasksScreen(
    taskViewModel: TaskViewModel,
    taskCategoryViewModel: TaskCategoryViewModel,
    navigationController: NavHostController,
    holidaysViewModel: HolidaysViewModel,
) {
    // Obtener el contexto en el composable usando `LocalContext`
    val context = LocalContext.current

    // Crea el launcher para solicitar permisos
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            if (isGranted) {
                Logger.debug("POST_NOTIFICATIONS", "Permission granted")
            } else {
                Logger.debug("POST_NOTIFICATIONS", "Permission denied")
            }
        }
    )

    // Establece el launcher en el servicio de permisos
    LaunchedEffect(Unit) {
        taskViewModel.setPermissionLauncher(permissionLauncher)
    }

    // Verifica si el permiso ya está concedido, de lo contrario, solicítalo
    LaunchedEffect(Unit) {
        if (!taskViewModel.isNotificationPermissionGranted(context)) {
            taskViewModel.requestNotificationPermission(context)
        }
    }

    // Recuperamos el estado de las tareas por fecha
    val uiStateByDate by taskViewModel.tasksByDateState.collectAsState()

    // Recuperamos todas las fechas de las tareas y días festivos
    val taskDates by taskViewModel.taskDatesFlow.collectAsState(emptyList())
    val holidays by holidaysViewModel.holidays.collectAsState(emptyList())

    val selectedDate: LocalDate by taskViewModel.selectedDate.observeAsState(LocalDate.now())
    val combinedDates = taskDates.union(holidays.map { it.date }).toList()
    val categoryUiState by taskCategoryViewModel.uiState.collectAsState()
    val categories = when (categoryUiState) {
        is TaskCategoryUiState.Success -> (categoryUiState as TaskCategoryUiState.Success).categories
        else -> emptyList()
    }

    // Maneja el estado general de tareas
    when (uiStateByDate) {
        is TasksUiState.Error -> {
            // Maneja el estado de error
        }

        TasksUiState.Loading -> {
            CircularProgressIndicator()
        }

        is TasksUiState.Success -> {
            Container(
                showDialog = taskViewModel.showDialog.collectAsState().value,
                taskViewModel = taskViewModel,
                tasks = (uiStateByDate as TasksUiState.Success).tasks,
                dates = combinedDates,
                holidays = holidays,
                navigationController = navigationController,
                selectedDate = selectedDate,
                categories = categories,
                context = context
            )
        }
    }
}

@Composable
fun Container(
    showDialog: Boolean,
    taskViewModel: TaskViewModel,
    tasks: List<TaskModel>,
    dates: List<LocalDate>,
    holidays: List<HolidayModel>, // Recibe la lista de días festivos
    navigationController: NavHostController,
    selectedDate: LocalDate,
    categories: List<TaskCategoryModel>,
    context: Context,
) {
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            CalendarComponent(
                initialDate = selectedDate,
                modifier = Modifier.padding(10.dp),
                taskDates = dates,
                onDateSelected = { date -> taskViewModel.setDate(date) } // De acuerdo al dia seleccionado mostrar las tareas de ese día
            )
            Text(
                modifier = Modifier.padding(horizontal = 15.dp),
                text = stringResource(string.tasks_title),
                fontSize = typography.titleLarge.fontSize,
                color = colorScheme.tertiary
            )
            Box(modifier = Modifier.padding(10.dp)) {
                TasksList(
                    tasks = tasks,
                    holidays = holidays,
                    selectedDate = selectedDate,
                    taskViewModel = taskViewModel,
                    navigationController = navigationController,
                    context = context
                )
            }
        }

        FabDialog(
            Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp),
            taskViewModel
        )
        if (showDialog) {
            BottomSheetComponent(
                selectedDate = selectedDate,
                onDismiss = { taskViewModel.onDialogClose() },
                onConfirm = { taskViewModel.onDialogClose() },
                placeholder = "Añade tu tarea",
                buttonText = "Agregar",
                categories = categories,
                taskViewModel = taskViewModel
            )
        }

    }
}


@Composable
fun TasksList(
    tasks: List<TaskModel>,
    holidays: List<HolidayModel>,
    selectedDate: LocalDate,
    taskViewModel: TaskViewModel,
    navigationController: NavHostController,
    context: Context,
) {
    var expandedId: String? by remember { mutableStateOf(null) }

    LazyColumn {
        items(tasks, key = { it.id }) { task ->
            Box {
                TaskItemComponent(
                    text = task.task,
                    checked = task.selected,
                    onClick = {
                        navigationController.navigate(EditTaskRoute(id = task.id))
                    },
                    onLongPress = {
                        expandedId = task.id
                    },
                    onCheckBoxChange = { isChecked ->
                        taskViewModel.updateTask(task.copy(selected = isChecked))
                    }
                )
                Box(
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                ) {
                    DropdownMenu(
                        expanded = expandedId == task.id,
                        onDismissRequest = { expandedId = null }
                    ) {
                        DropdownMenuItem(
                            text = { Text(stringResource(string.dw_delete)) },
                            onClick = { taskViewModel.updateTask(task.copy(stateId = DELETED_ID)) }
                        )
                    }
                }
            }
        }
        items(holidays.filter { it.date == selectedDate }) { holiday ->
            HolidayItem(holiday = holiday)
        }
    }
}

@Composable
fun HolidayItem(holiday: HolidayModel) {
    Card(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 5.dp, vertical = 5.dp)
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .background(colorScheme.tertiaryContainer.copy(alpha = 0.2f))
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(drawable.ic_outline_calendar_),
                contentDescription = stringResource(ic_calendar),
                tint = colorScheme.tertiary,
                modifier = Modifier.padding(end = 10.dp)
            )
            Text(
                text = "${holiday.date}: ${holiday.name}",
                color = colorScheme.tertiary
            )
        }
    }
}

@Composable
fun FabDialog(modifier: Modifier, taskViewModel: TaskViewModel) {
    FloatingActionButton(onClick = {
        taskViewModel.onShowDialogClick()
    }, modifier = modifier) {
        Icon(Icons.Filled.Add, contentDescription = "Boton para agregar tareas")
    }
}