package com.example.todoapp.addtasks.ui

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.shapes
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.todoapp.R
import com.example.todoapp.addtasks.ui.model.TaskModel
import com.example.todoapp.ui.components.AdvancedTimePickerComponent
import com.example.todoapp.ui.components.CalendarComponent
import com.example.todoapp.ui.components.CalendarStyle
import com.example.todoapp.ui.components.TextFieldComponent
import com.example.todoapp.ui.theme.Typography
import org.threeten.bp.LocalDate

@Composable
fun EditTaskScreen(taskViewModel: TaskViewModel, id: Int) {
    // Obtener la tarea por primera vez
    LaunchedEffect(id) {
        taskViewModel.getTaskById(id)
    }

    val taskUiState by taskViewModel.taskUiState.observeAsState(TaskUiState.Empty)
    val showDialog: Boolean by taskViewModel.showDialog.observeAsState(false)
    val showTimePicker: Boolean by taskViewModel.showTimePicker.observeAsState(false)

    when (taskUiState) {
        is TaskUiState.Loading -> {
            CircularProgressIndicator()
        }

        is TaskUiState.Error -> {
            Text(text = "Error al cargar la tarea.")
        }

        is TaskUiState.Success -> {
            Container(
                showDialog,
                showTimePicker,
                taskViewModel,
                (taskUiState as TaskUiState.Success).task
            )
        }

        is TaskUiState.Empty -> {
            Text(text = "Tarea no encontrada.")
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Container(
    showDialog: Boolean,
    showTimePicker: Boolean,
    taskViewModel: TaskViewModel,
    task: TaskModel,
) {

    var taskText by remember { mutableStateOf(task.task) }
    var taskDetail by remember { mutableStateOf("") }

    var selectedDate by remember { mutableStateOf<LocalDate?>(null) }

    var selectedTime: TimePickerState? by remember { mutableStateOf(null) }

    Box(modifier = Modifier.fillMaxSize()) {
        Column {
            TextField(
                value = taskText,
                textStyle = Typography.bodyLarge,
                onValueChange = {
                    taskText = it
                    taskViewModel.updateTask(task.copy(task = taskText)) // Actualizar la tarea en el ViewModel
                },
                placeholder = { Text(text = "Editar tarea", style = Typography.bodyLarge) },
                colors = TextFieldDefaults.colors(
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    cursorColor = colorScheme.onSurface.copy(alpha = 0.5f),
                    focusedContainerColor = colorScheme.errorContainer,
                    unfocusedContainerColor = colorScheme.errorContainer,
                ),
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions.Default.copy(
                    imeAction = ImeAction.Done // Para que el teclado se cierre
                )
            )

            TextFieldComponent(
                leadingIcon = {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_notes),
                        contentDescription = "Icono para agregar detalles",
                        modifier = Modifier.padding(end = 6.dp)
                    )
                },
                value = taskDetail,
                textStyle = Typography.bodyMedium,
                onValueChange = { taskDetail = it },
                placeholder = "Agregar detalles",
                modifier = Modifier
                    .fillMaxWidth()
                    .background(colorScheme.secondaryContainer)
                    .padding(horizontal = 7.dp, vertical = 7.dp),
            )

            TextButton(
                onClick = { taskViewModel.onShowDialogClick() },
                modifier = Modifier
                    .fillMaxWidth()
                    .background(colorScheme.secondaryContainer)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Start,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_access_time),
                        contentDescription = "Icono para agregar fecha/hora",
                        tint = colorScheme.onBackground,
                        modifier = Modifier.padding(start = 2.dp, end = 6.dp)
                    )
                    Text(
                        text = "Agregar Fecha/Hora",
                        color = colorScheme.onBackground,
                        style = Typography.bodyMedium
                    )
                }
            }
        }


        if (showDialog) {
            Dialog(
                onDismissRequest = { taskViewModel.onDialogClose() },
                properties = DialogProperties(usePlatformDefaultWidth = false),
            ) {
                Surface(
                    shape = shapes.extraLarge,
                    tonalElevation = 6.dp,
                    modifier =
                    Modifier
                        .wrapContentSize()
                        .background(
                            shape = shapes.extraLarge,
                            color = colorScheme.surface
                        )
                        .padding(horizontal = 30.dp),
                ) {
                    Column {
                        CalendarComponent(
                            modifier = Modifier.padding(20.dp),
                            style = CalendarStyle.MaxRow,
                            onDateSelected = { date ->
                                selectedDate = date
                                Log.d("date", "Fecha seleccionada: $date")
                            }
                        )
                        HorizontalDivider(
                            thickness = 2.dp
                        )
                        TextButton(
                            onClick = { taskViewModel.onShowTimePicker() },
                            modifier = Modifier
                                .fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.Start,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_access_time),
                                    contentDescription = "Icono para agregar fecha/hora",
                                    tint = colorScheme.onBackground,
                                    modifier = Modifier.padding(start = 2.dp, end = 6.dp)
                                )
                                Text(
                                    text = "Agregar Fecha/Hora",
                                    color = colorScheme.onBackground,
                                    style = Typography.bodyMedium
                                )
                            }
                        }

                        HorizontalDivider(
                            thickness = 2.dp
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {
                            TextButton(onClick = { taskViewModel.onDialogClose() }) {
                                Text(text = "Cancelar")
                            }
                            TextButton(onClick = { taskViewModel.onDialogClose() }) {
                                Text(text = "Aceptar")
                            }
                        }
                    }
                }
            }
        }

        if (showTimePicker) {
            AdvancedTimePickerComponent(
                onConfirm = { timePickerState ->
                    selectedTime = timePickerState
                    taskViewModel.onHideTimePicker()
                },
                onDismiss = {
                    taskViewModel.onHideTimePicker()
                }
            )
        }
    }
}