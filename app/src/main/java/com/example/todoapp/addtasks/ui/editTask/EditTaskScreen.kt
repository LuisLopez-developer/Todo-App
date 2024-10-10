@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.todoapp.addtasks.ui.editTask

import android.content.Context
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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.todoapp.R.drawable
import com.example.todoapp.R.string
import com.example.todoapp.addtasks.ui.model.TaskModel
import com.example.todoapp.addtasks.ui.utils.formatDate
import com.example.todoapp.addtasks.ui.utils.formatTime
import com.example.todoapp.taskcategory.ui.TaskCategoryUiState
import com.example.todoapp.taskcategory.ui.TaskCategoryViewModel
import com.example.todoapp.ui.components.AlertDialogComponent
import com.example.todoapp.ui.components.DatePickerDialogComponent
import com.example.todoapp.ui.layouts.SharedViewModel
import com.example.todoapp.ui.theme.Typography
import org.threeten.bp.LocalDate

@Composable
fun EditTaskScreen(
    taskCategoryViewModel: TaskCategoryViewModel,
    taskEditViewModel: TaskEditViewModel,
    id: String,
    sharedViewModel: SharedViewModel,
    navController: NavHostController,
) {
    // Establecer el ID de la tarea en el ViewModel
    LaunchedEffect(id) {
        taskEditViewModel.setTaskId(id)
    }

    val uiStateById by taskEditViewModel.taskByIdState.collectAsState(TaskUiState.Loading)

    val showDatePicker by taskEditViewModel.showDatePicker.collectAsState()

    // Observar el modelo de las categorias
    val categoryUiState by taskCategoryViewModel.uiState.collectAsState()

    // Obtener el contexto en el composable usando `LocalContext`
    val context = LocalContext.current

    when (uiStateById) {
        is TaskUiState.Loading -> {
            Box(modifier = Modifier.fillMaxSize()) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }
        }

        is TaskUiState.Error -> {
            Text(text = "Error al cargar la tarea.")
        }

        is TaskUiState.Success -> {
            Main(
                taskEditViewModel = taskEditViewModel,
                taskCategoryViewModel = taskCategoryViewModel,
                sharedViewModel = sharedViewModel,
                navController = navController,
                task = (uiStateById as TaskUiState.Success).task,
                categoryUiState = categoryUiState,
                context = context,
                showDatePicker = showDatePicker
            )
        }

        is TaskUiState.Empty -> {
            Text(text = "Tarea no encontrada.")
        }
    }
}

@Composable
fun Main(
    taskEditViewModel: TaskEditViewModel,
    taskCategoryViewModel: TaskCategoryViewModel,
    sharedViewModel: SharedViewModel,
    navController: NavHostController,
    task: TaskModel,
    categoryUiState: TaskCategoryUiState,
    context: Context,
    showDatePicker: Boolean,
) {
    ConfigTopBar(sharedViewModel, navController, taskEditViewModel, task)
    Container(
        taskEditViewModel,
        showDatePicker,
        taskCategoryViewModel,
        task,
        categoryUiState,
        context
    )
}

@Composable
fun ConfigTopBar(
    sharedViewModel: SharedViewModel,
    navController: NavHostController,
    taskEditViewModel: TaskEditViewModel,
    task: TaskModel,
) {
    var openDropdownMenu by remember { mutableStateOf(false) }
    var openAlertDialog by remember { mutableStateOf(false) }

    sharedViewModel.topBarTitle.value = ""
    sharedViewModel.topBarNavigationIcon.value = {
        IconButton(onClick = { navController.popBackStack() }) {
            Icon(
                painter = painterResource(id = drawable.ic_arrow_back),
                contentDescription = stringResource(id = string.ic_arrow_back),
                tint = colorScheme.onPrimaryContainer,
            )
        }
    }
    sharedViewModel.topBarActions.value = {
        Box {
            IconButton(onClick = {
                openDropdownMenu = true
            }) {
                Icon(
                    painter = painterResource(id = drawable.ic_more_vert),
                    contentDescription = stringResource(id = string.ic_more_vert),
                    tint = colorScheme.onPrimaryContainer,
                )
            }
            DropdownMenu(
                expanded = openDropdownMenu,
                onDismissRequest = { openDropdownMenu = false }
            ) {
                DropdownMenuItem(
                    text = {
                        Text(
                            text = if (task.selected) stringResource(string.mark_undone) else stringResource(
                                string.mark_done
                            )
                        )
                    },
                    onClick = {
                        openDropdownMenu = false
                        taskEditViewModel.updateTask(task.copy(selected = task.selected.not()))
                    }
                )
                DropdownMenuItem(text = { Text(text = stringResource(id = string.dw_delete)) },
                    onClick = {
                        openDropdownMenu = false
                        openAlertDialog = true
                    }
                )
            }
        }
    }

    if (openAlertDialog) {
        AlertDialogComponent(
            onDismissRequest = { openAlertDialog = false },
            title = stringResource(string.question_delete_task),
            message = stringResource(string.question_delete_task_message),
            onConfirm = {
                openAlertDialog = false
                taskEditViewModel.onDeleted(task)
                navController.popBackStack()
            },
            timer = 0
        )
    }
}

@Composable
fun Container(
    taskEditViewModel: TaskEditViewModel,
    showDatePicker: Boolean,
    taskCategoryViewModel: TaskCategoryViewModel,
    task: TaskModel,
    categoryUiState: TaskCategoryUiState,
    context: Context,
) {
    val categories = when (categoryUiState) {
        is TaskCategoryUiState.Success -> categoryUiState.categories
        else -> emptyList()
    }
    val isDropDownExpanded by taskCategoryViewModel.showDropDown.observeAsState(false)

    val alpha = if (task.selected) 0.4f else 1f

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
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
                        .background(color = colorScheme.surfaceContainer)
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
                                value = categories.find { it.id == task.categoryId }?.category
                                    ?: stringResource(string.uncategorized),
                                onValueChange = {},
                                textStyle = typography.bodyMedium.copy(
                                    color = colorScheme.onSurface.copy(
                                        alpha
                                    )
                                ),
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
                                            modifier = Modifier
                                                .padding(end = 6.dp)
                                                .alpha(alpha)
                                        )
                                        Box(
                                            modifier = Modifier.weight(1f)
                                        ) { innerTextField() }
                                        Icon(
                                            painter = painterResource(id = drawable.ic_outline_arrow_down),
                                            contentDescription = stringResource(string.ic_arrow_drop_down),
                                            modifier = Modifier.alpha(alpha)
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
                                        taskEditViewModel.updateTask(task.copy(categoryId = null))
                                    }
                                )

                                // Listar los elementos disponibles
                                categories
                                    .forEach { item ->
                                        DropdownMenuItem(
                                            text = { Text(text = item.category) },
                                            onClick = {
                                                taskEditViewModel.updateTask(task.copy(categoryId = item.id))
                                                taskCategoryViewModel.setShowDropDown(false)
                                            }
                                        )
                                    }
                            }
                        }

                        TextField(
                            value = task.task,
                            textStyle = Typography.bodyLarge.copy(colorScheme.onSurface.copy(alpha)),
                            onValueChange = {
                                taskEditViewModel.updateTask(task.copy(task = it))
                            },
                            placeholder = {
                                Text(
                                    text = stringResource(string.edit_task_title),
                                    style = Typography.bodyLarge.copy(
                                        colorScheme.onSurface.copy(alpha)
                                    )
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
                            value = task.details ?: "",
                            onValueChange = {
                                taskEditViewModel.updateTask(task.copy(details = it))
                            },
                            textStyle = Typography.bodySmall.copy(
                                color = colorScheme.onSurface.copy(alpha)
                            ),
                            decorationBox = { innerTextField ->
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Icon(
                                        painter = painterResource(id = drawable.ic_notes),
                                        contentDescription = stringResource(string.ic_notes),
                                        modifier = Modifier
                                            .padding(end = 6.dp)
                                            .alpha(alpha)
                                    )
                                    Box(modifier = Modifier.weight(1f)) {
                                        if (task.details.isNullOrEmpty()) {
                                            Text(
                                                text = stringResource(string.edit_task_details),
                                                style = Typography.bodySmall.copy(
                                                    textAlign = TextAlign.Center,
                                                    color = colorScheme.onSurface.copy(alpha)
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
                        .background(color = colorScheme.surfaceContainer)
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
                        textStyle = typography.bodyMedium.copy(
                            color = colorScheme.onSurface.copy(
                                alpha
                            )
                        ),
                        readOnly = true,
                        decorationBox = { innerTextField ->
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable(
                                        enabled = task.selected.not(),
                                        onClick = { taskEditViewModel.onShowDateDialogClick() })
                            ) {
                                Icon(
                                    painter = painterResource(id = drawable.ic_outline_calendar_),
                                    contentDescription = stringResource(string.ic_calendar),
                                    modifier = Modifier
                                        .padding(end = 6.dp)
                                        .alpha(alpha)
                                )
                                Box(
                                    modifier = Modifier.weight(1f)
                                ) { innerTextField() }
                                Icon(
                                    painter = painterResource(id = drawable.ic_outline_arrow_down),
                                    contentDescription = stringResource(string.ic_arrow_drop_down),
                                    modifier = Modifier.alpha(alpha)
                                )
                            }
                        }
                    )
                }
            }
        }
    }

    // Mostrar DatePickerDialog si `showDatePicker` es verdadero y si la tarea no está seleccionada
    if (showDatePicker && task.selected.not()) {
        DatePickerDialogComponent(
            initialDate = task.startDate,
            initialTime = task.time,
            onDateSelected = { date ->
                taskEditViewModel.setTemporaryDate(date)
            },
            onTimeSelected = { time ->
                taskEditViewModel.setTemporaryTime(time)
            },
            onDismiss = { taskEditViewModel.onHideDatePicker() },
            onConfirm = {
                val updatedTask = task.copy(
                    startDate = taskEditViewModel.temporaryDate.value ?: LocalDate.now(),
                    time = taskEditViewModel.temporaryTime.value
                )
                taskEditViewModel.onDateAndTime(updatedTask, context)
                taskEditViewModel.resetTemporaryDateTime()
            }
        )
    }
}