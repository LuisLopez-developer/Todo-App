package com.example.todoapp.addtasks.ui

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
import com.example.todoapp.addtasks.ui.utils.formatDate
import com.example.todoapp.addtasks.ui.utils.formatTime
import com.example.todoapp.ui.components.AdvancedTimePickerComponent
import com.example.todoapp.ui.components.CalendarComponent
import com.example.todoapp.ui.components.TextFieldComponent
import com.example.todoapp.ui.components.TextFieldWithButtonComponent
import com.example.todoapp.ui.theme.Typography
import org.threeten.bp.LocalDate
import org.threeten.bp.LocalTime

@Composable
fun EditTaskScreen(taskViewModel: TaskViewModel, id: Int) {
    // Obtener la tarea por primera vez
    LaunchedEffect(id) {
        taskViewModel.getTaskById(id)
    }

    val taskUiState by taskViewModel.taskUiState.observeAsState(TaskUiState.Empty)
    val showDatePicker: Boolean by taskViewModel.showDatePicker.observeAsState(false)
    val showTimePicker: Boolean by taskViewModel.showTimePicker.observeAsState(false)
    var localTask by remember { mutableStateOf<TaskModel?>(null) }

    when (taskUiState) {
        is TaskUiState.Loading -> {
            CircularProgressIndicator()
        }

        is TaskUiState.Error -> {
            Text(text = "Error al cargar la tarea.")
        }

        is TaskUiState.Success -> {
            localTask = (taskUiState as TaskUiState.Success).task
            Container(
                showDatePicker,
                showTimePicker,
                taskViewModel,
                localTask!!
            ) { updatedTask ->
                localTask = updatedTask
            }
        }

        is TaskUiState.Empty -> {
            Text(text = "Tarea no encontrada.")
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Container(
    showDatePicker: Boolean,
    showTimePicker: Boolean,
    taskViewModel: TaskViewModel,
    task: TaskModel,
    onTaskUpdated: (TaskModel) -> Unit, // Callback para actualizar la tarea localmente
) {
    var taskText by remember { mutableStateOf(task.task) }
    var taskDetail by remember { mutableStateOf(task.details ?: "") }
    val temporaryDate by taskViewModel.temporaryDate.observeAsState(task.startDate)
    val temporaryTime by taskViewModel.temporaryTime.observeAsState(task.time)

    Box(modifier = Modifier.fillMaxSize()) {
        Column(verticalArrangement = Arrangement.Top) {
            TextField(
                value = taskText,
                textStyle = Typography.bodyLarge,
                onValueChange = {
                    taskText = it
                    val updatedTask = task.copy(task = taskText)
                    onTaskUpdated(updatedTask)
                },
                placeholder = { Text(text = "Editar tarea", style = Typography.bodyLarge) },
                colors = TextFieldDefaults.colors(
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    cursorColor = colorScheme.onSurface.copy(alpha = 0.5f),
                    focusedContainerColor = colorScheme.secondaryContainer,
                    unfocusedContainerColor = colorScheme.secondaryContainer,
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
                onValueChange = {
                    taskDetail = it
                    val updatedTask = task.copy(details = taskDetail)
                    onTaskUpdated(updatedTask)
                },
                placeholder = "Agregar detalles",
                modifier = Modifier
                    .fillMaxWidth()
                    .background(colorScheme.secondaryContainer)
                    .padding(start = 15.5.dp, end = 12.5.dp, top = 7.dp, bottom = 7.dp)
            )

            TextButton(
                onClick = { taskViewModel.onShowDateDialogClick() },
                modifier = Modifier
                    .fillMaxWidth()
                    .background(colorScheme.secondaryContainer)
                    .padding(start = 3.5.dp)
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
                        modifier = Modifier.padding(end = 6.dp)
                    )

                    if (task.startDate != null) {
                        val formattedDate = formatDate(task.startDate)
                        var formattedTime = task.time?.let { formatTime(it) }

                        formattedTime = if (formattedTime.isNullOrEmpty()) {
                            ""
                        } else {
                            ", $formattedTime"
                        }

                        TextFieldWithButtonComponent(
                            text = "$formattedDate $formattedTime",
                            onIconClick = {
                                taskViewModel.resetTaskDateTime(task.id)
                                taskViewModel.resetTemporaryDateTime()
                            }
                        )

                    } else {
                        Text(
                            text = "Agregar Fecha/Hora",
                            color = colorScheme.onBackground,
                            style = Typography.bodyMedium
                        )
                    }
                }
            }
        }

        if (showDatePicker) {
            Dialog(
                onDismissRequest = { taskViewModel.onHideDatePicker() },
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
                            initialDate = temporaryDate
                                ?: LocalDate.now(), // Usa el valor temporal o la fecha actual
                            onDateSelected = { date ->
                                taskViewModel.setTemporaryDate(date)
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

                                // Verifica si se ha seleccionado una hora
                                if (temporaryTime != null) {
                                    val formattedTime = formatTime(temporaryTime!!)
                                    TextFieldWithButtonComponent(
                                        text = formattedTime,
                                        onIconClick = {
                                            taskViewModel.setTemporaryTime(null)
                                        })
                                } else {
                                    Text(
                                        text = "Agregar Hora",
                                        color = colorScheme.onBackground,
                                        style = Typography.bodyMedium
                                    )
                                }
                            }
                        }

                        HorizontalDivider(
                            thickness = 2.dp
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {
                            TextButton(onClick = { taskViewModel.onHideDatePicker() }) {
                                Text(text = "Cancelar")
                            }
                            TextButton(onClick = {
                                // Actualiza la tarea y localmente para reflejar los cambios
                                val updatedTask = task.copy(
                                    startDate = taskViewModel.temporaryDate.value
                                        ?: LocalDate.now(), // Usa la fecha actual si es null
                                    time = taskViewModel.temporaryTime.value
                                )
                                onTaskUpdated(updatedTask)
                                taskViewModel.updateTask(updatedTask)
                                taskViewModel.resetTemporaryDateTime()
                                taskViewModel.onHideDatePicker()
                            }) {
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
                    taskViewModel.setTemporaryTime(
                        LocalTime.of(
                            timePickerState.hour,
                            timePickerState.minute
                        )
                    )
                    taskViewModel.onHideTimePicker()
                },
                onDismiss = {
                    taskViewModel.onHideTimePicker()
                }
            )
        }
    }
}
