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
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavHostController
import com.example.todoapp.addtasks.ui.model.TaskModel
import com.example.todoapp.holidays.ui.HolidaysViewModel
import com.example.todoapp.taskcategory.ui.TaskCategoryViewModel
import com.example.todoapp.ui.components.BottomSheetComponent
import com.example.todoapp.ui.components.CalendarComponent
import com.example.todoapp.ui.navigation.EditTaskRoute
import org.threeten.bp.LocalDate

@Composable
fun TasksScreen(
    taskViewModel: TaskViewModel,
    taskCategoryViewModel: TaskCategoryViewModel,
    navigationController: NavHostController,
    holidaysViewModel: HolidaysViewModel,
) {
    val lifecycle = LocalLifecycleOwner.current.lifecycle

    // Recuperamos el estado de las tareas por fecha
    val uiStateByDate by produceState<TasksUiState>(
        initialValue = TasksUiState.Loading,
        key1 = lifecycle,
        key2 = taskViewModel
    ) {
        lifecycle.repeatOnLifecycle(state = Lifecycle.State.STARTED) {
            taskViewModel.tasksByDateState.collect { value = it }
        }
    }

    // Recuperamos todas las fechas de las tareas
    val taskDates by taskViewModel.taskDatesFlow.collectAsState(emptyList())
    val holidays by holidaysViewModel.holidays.collectAsState(emptyList())

    val combinedDates = taskDates.union(holidays.map { it.date }).toList()

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
                taskCategoryViewModel = taskCategoryViewModel,
                tasks = (uiStateByDate as TasksUiState.Success).tasks,
                dates = combinedDates, // Pasa todas las fechas de tareas
                navigationController = navigationController
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
    dates: List<LocalDate>, // Recibe la lista de fechas
    navigationController: NavHostController,
) {
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            CalendarComponent(
                taskDates = dates,
                onDateSelected = { date -> taskViewModel.setDate(date) } // De acuerdo al dia seleccionado mostrar las tareas de ese día
            )
            TasksList(tasks, taskViewModel, navigationController)
        }

        FabDialog(
            Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp),
            taskViewModel
        )

        BottomSheetComponent(
            showSheet = showDialog,
            onDismiss = { taskViewModel.onDialogClose() },
            onConfirm = { taskText, selectedCategory ->
                taskViewModel.onTaskCreated(taskText, selectedCategory = selectedCategory)
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