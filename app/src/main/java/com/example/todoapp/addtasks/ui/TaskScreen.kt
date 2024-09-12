package com.example.todoapp.addtasks.ui

import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.todoapp.addtasks.ui.model.TaskModel
import com.example.todoapp.taskcategory.ui.TaskCategoryViewModel
import com.example.todoapp.ui.components.BottomSheetComponent
import com.example.todoapp.ui.components.CalendarComponent
import com.example.todoapp.ui.navigation.EditTaskRoute
import org.threeten.bp.LocalDate

@Composable
fun TasksScreen(taskViewModel: TaskViewModel,taskCategoryViewModel: TaskCategoryViewModel, navigationController: NavHostController) {
    val showDialog: Boolean by taskViewModel.showDialog.collectAsState(false)

    val uiState by taskViewModel.uiState.collectAsState()
    val tasksByDateState by taskViewModel.tasksByDateState.collectAsState()

    // Estado para la fecha seleccionada en el calendario
    var selectedDate by remember { mutableStateOf<LocalDate>(LocalDate.now()) }

    // Llama a fetchTasksByDate si la fecha seleccionada cambia
    selectedDate.let { date ->
        taskViewModel.fetchTasksByDate(date)
    }

    // Filtrar tareas basadas en la fecha seleccionada
    val filteredTasks = tasksByDateState.let { state ->
        when (state) {
            is TasksUiState.Success -> {
                state.tasks.filter {
                    it.startDate == selectedDate
                }
            }

            else -> emptyList()
        }
    }

    // Maneja el estado general de tareas
    when (uiState) {
        is TasksUiState.Error -> {
            // Maneja el estado de error
        }

        TasksUiState.Loading -> {
            CircularProgressIndicator()
        }

        is TasksUiState.Success -> {
            Container(
                showDialog,
                taskViewModel,
                taskCategoryViewModel,
                filteredTasks,
                navigationController,
                onDateSelected = { date ->
                    selectedDate = date
                }
            )
        }
    }
}

@Composable
fun Container(
    showDialog: Boolean,
    taskViewModel: TaskViewModel,
    taskCategoryViewModel: TaskCategoryViewModel,
    tasks: List<TaskModel>,
    navigationController: NavHostController,
    onDateSelected: (LocalDate) -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
        ) {
            val taskDates = tasks
                .map { it.startDate } // Asegúrate de que solo se muestren fechas válidas

            CalendarComponent(
                taskDates = taskDates,
                onDateSelected = { date -> onDateSelected(date) } // Callback para la selección de fechas
            )
            TasksList(tasks, taskViewModel, navigationController)
        }

        FabDialog(
            Modifier
                .align(Alignment.BottomEnd) // Alínea el FAB al final de la caja (absoluto)
                .padding(16.dp),
            taskViewModel
        )

        BottomSheetComponent(
            showSheet = showDialog,
            onDismiss = { taskViewModel.onDialogClose() },
            onConfirm = { taskText ->
                taskViewModel.onTaskCreated(taskText)
            },
            placeholder = "Añade tu tarea",
            buttonText = "Agregar",
            initialText = "",
            taskCategoryViewModel = taskCategoryViewModel
        )
    }
}


@Composable
fun TasksList(
    tasks: List<TaskModel>,
    taskViewModel: TaskViewModel,
    navigationController: NavHostController,
) {
    LazyColumn {
        items(tasks, key = { it.id }) { task ->
            ItemTask(taskModel = task, taskViewModel = taskViewModel, navigationController)
        }
    }
}

@Composable
fun ItemTask(
    taskModel: TaskModel,
    taskViewModel: TaskViewModel,
    navigationController: NavHostController,
) {
    var isLongPress by remember { mutableStateOf(false) }

    Card(
        Modifier
            .fillMaxWidth()
            .pointerInput(Unit) {
                detectTapGestures(
                    onLongPress = {
                        isLongPress = true
                        taskViewModel.onItemRemove(taskModel)
                    },
                    onTap = {
                        if (!isLongPress) {
                            navigationController.navigate(
                                EditTaskRoute(
                                    id = taskModel.id
                                )
                            )
                        }
                    }
                )
            }
    ) {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Checkbox(
                checked = taskModel.selected,
                onCheckedChange = { taskViewModel.onCheckBox(taskModel) }
            )
            Text(
                text = taskModel.task,
                Modifier.weight(1f)
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