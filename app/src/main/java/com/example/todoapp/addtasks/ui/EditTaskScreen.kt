package com.example.todoapp.addtasks.ui

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.todoapp.R.drawable
import com.example.todoapp.R.string
import com.example.todoapp.addtasks.ui.model.TaskModel
import com.example.todoapp.addtasks.ui.utils.formatDate
import com.example.todoapp.addtasks.ui.utils.formatTime
import com.example.todoapp.taskcategory.ui.TaskCategoryUiState
import com.example.todoapp.taskcategory.ui.TaskCategoryViewModel
import com.example.todoapp.ui.components.DatePickerDialogComponent
import com.example.todoapp.ui.components.TimePickerDialogComponent
import com.example.todoapp.ui.theme.Typography
import org.threeten.bp.LocalDate

@Composable
fun EditTaskScreen(
    taskViewModel: TaskViewModel,
    taskCategoryViewModel: TaskCategoryViewModel,
    id: Int,
) {
    // Observe the StateFlow changes for the task UI state and other states
    val taskUiState by taskViewModel.taskFlowUiState.collectAsState()
    val showDatePicker by taskViewModel.showDatePicker.collectAsState()
    val showTimePicker by taskViewModel.showTimePicker.collectAsState()

    // Observar el modelo de las categorias
    val categoryUiState by taskCategoryViewModel.uiState.collectAsState()

    // Recibe y carga el task con el id correcto
    LaunchedEffect(id) {
        taskViewModel.getTaskById(id)
    }


    when (taskUiState) {
        is TaskUiState.Loading -> {
            CircularProgressIndicator()
        }

        is TaskUiState.Error -> {
            Text(text = "Error al cargar la tarea.")
        }

        is TaskUiState.Success -> {
            Container(
                showDatePicker,
                showTimePicker,
                taskViewModel,
                taskCategoryViewModel,
                (taskUiState as TaskUiState.Success).task,
                categoryUiState
            )
        }

        is TaskUiState.Empty -> {
            Text(text = "Tarea no encontrada.")
        }
    }
}

@Composable
fun Container(
    showDatePicker: Boolean,
    showTimePicker: Boolean,
    taskViewModel: TaskViewModel,
    taskCategoryViewModel: TaskCategoryViewModel,
    task: TaskModel,
    categoryUiState: TaskCategoryUiState,
) {
    var taskText by remember { mutableStateOf(task.task) }
    var taskDetail by remember { mutableStateOf(task.details ?: "") }

    val categories = when (categoryUiState) {
        is TaskCategoryUiState.Success -> categoryUiState.categories
        else -> emptyList()
    }
    val isDropDownExpanded by taskCategoryViewModel.showDropDown.observeAsState(false)

    Log.i("task", task.toString())

    Box(modifier = Modifier.fillMaxSize()) {
        Column {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(15.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .wrapContentHeight()
                        .fillMaxWidth()
                        .background(color = colorScheme.surface)
                        .align(Alignment.CenterHorizontally)
                ) {
                    Column(
                        modifier = Modifier
                            .padding(15.dp)
                            .fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box {
                            BasicTextField(
                                value = task.category ?: stringResource(string.uncategorized),
                                onValueChange = {},
                                textStyle = typography.bodyMedium,
                                readOnly = true,
                                decorationBox = { innerTextField ->
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable {
                                                taskCategoryViewModel.setShowDropDown(true)
                                            }
                                    ) {

                                        Icon(
                                            painter = painterResource(id = drawable.ic_outline_label_offer),
                                            contentDescription = stringResource(string.ic_label_offer),
                                            modifier = Modifier.padding(end = 6.dp)
                                        )
                                        Box(
                                            modifier = Modifier.weight(1f)
                                        ) { innerTextField() }
                                        Icon(
                                            painter = painterResource(id = drawable.ic_outline_arrow_down),
                                            contentDescription = stringResource(string.ic_arrow_drop_down)
                                        )
                                    }
                                }
                            )

                            DropdownMenu(
                                expanded = isDropDownExpanded,
                                onDismissRequest = { taskCategoryViewModel.setShowDropDown(false) }
                            ) {
                                // Opción para el valor predeterminado
                                DropdownMenuItem(
                                    text = { Text(text = stringResource(string.uncategorized)) },
                                    onClick = {
                                        taskCategoryViewModel.setSelectedCategory("Sin categoría")
                                        // Si la categoría es el item que no tiene nada se actualiza a null
                                        taskViewModel.updateTask(task.copy(category = null))
                                    }
                                )

                                // Listar los elementos disponibles
                                categories
                                    .map { it.category }
                                    .forEach { item ->
                                        DropdownMenuItem(
                                            text = { Text(text = item) },
                                            onClick = {
                                                taskViewModel.updateTask(task.copy(category = item))
                                                taskCategoryViewModel.setShowDropDown(false)
                                            }
                                        )
                                    }
                            }
                        }


                        TextField(
                            value = taskText,
                            textStyle = Typography.bodyLarge,
                            onValueChange = {
                                taskText = it
                                taskViewModel.updateTask(task.copy(task = it))
                            },
                            placeholder = {
                                Text(
                                    text = stringResource(string.edit_task_title),
                                    style = Typography.bodyLarge
                                )
                            },
                            colors = TextFieldDefaults.colors(
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent,
                                cursorColor = colorScheme.onSurface.copy(alpha = 0.5f),
                                focusedContainerColor = Color.Transparent,
                                unfocusedContainerColor = Color.Transparent,
                            ),
                            modifier = Modifier.fillMaxWidth(),
                            keyboardOptions = KeyboardOptions.Default.copy(
                                imeAction = ImeAction.Done // Para que el teclado se cierre
                            )
                        )

                        BasicTextField(
                            value = taskDetail,
                            onValueChange = {
                                taskDetail = it
                                taskViewModel.updateTask(task.copy(details = it))
                            },
                            textStyle = typography.bodySmall,
                            decorationBox = { innerTextField ->
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Icon(
                                        painter = painterResource(id = drawable.ic_notes),
                                        contentDescription = stringResource(string.ic_notes),
                                        modifier = Modifier.padding(end = 6.dp)
                                    )
                                    Box(modifier = Modifier.weight(1f)) {
                                        if (taskDetail.isEmpty()) {
                                            Text(
                                                text = stringResource(string.edit_task_details), // Your placeholder text
                                                style = typography.bodySmall.copy(
                                                    textAlign = TextAlign.Center
                                                )
                                            )
                                        }
                                        innerTextField()
                                    }
                                }
                            }
                        )

                    }
                }
            }

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 15.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .wrapContentHeight()
                        .fillMaxWidth()
                        .background(color = colorScheme.surface)
                        .align(Alignment.CenterHorizontally)
                        .padding(16.dp)
                ) {
                    val formattedDate = formatDate(task.startDate)
                    val formattedTime = task.time?.let { formatTime(it) }
                    val displayText =
                        "$formattedDate${formattedTime?.let { ", $it" } ?: ""}"

                    BasicTextField(
                        value = displayText,
                        onValueChange = {},
                        textStyle = typography.bodyMedium,
                        readOnly = true,
                        decorationBox = { innerTextField ->
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { taskViewModel.onShowDateDialogClick() }
                            ) {
                                Icon(
                                    painter = painterResource(id = drawable.ic_outline_label_offer),
                                    contentDescription = stringResource(string.ic_label_offer),
                                    modifier = Modifier.padding(end = 6.dp)
                                )
                                Box(
                                    modifier = Modifier.weight(1f)
                                ) { innerTextField() }
                                Icon(
                                    painter = painterResource(id = drawable.ic_outline_arrow_down),
                                    contentDescription = stringResource(string.ic_arrow_drop_down)
                                )
                            }
                        }
                    )
                }

            }

        }
    }
    // Mostrar DateTimePickerDialog si `showDatePicker` es verdadero
    if (showDatePicker) {
        DatePickerDialogComponent(
            initialDate = task.startDate,
            taskViewModel = taskViewModel,
            onDismiss = { taskViewModel.onHideDatePicker() },
            onConfirm = {
                val updatedTask = task.copy(
                    startDate = taskViewModel.temporaryDate.value
                        ?: LocalDate.now(),
                    time = taskViewModel.temporaryTime.value
                )
                taskViewModel.updateTask(updatedTask)
                taskViewModel.resetTemporaryDateTime()
            }
        )
    }

    // Mostrar TimePickerDialog si `showTimePicker` es verdadero
    if (showTimePicker) {
        TimePickerDialogComponent(
            taskViewModel = taskViewModel,
            onDismiss = { taskViewModel.onHideTimePicker() }
        )
    }
}