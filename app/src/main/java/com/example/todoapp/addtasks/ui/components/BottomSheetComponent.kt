package com.example.todoapp.addtasks.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.PopupProperties
import com.example.todoapp.R
import com.example.todoapp.addtasks.ui.TaskViewModel
import com.example.todoapp.addtasks.ui.model.TaskModel
import com.example.todoapp.addtasks.ui.utils.formatDate
import com.example.todoapp.addtasks.ui.utils.formatTime
import com.example.todoapp.taskcategory.ui.model.TaskCategoryModel
import com.example.todoapp.ui.components.DatePickerDialogComponent
import com.example.todoapp.ui.components.DropdownMenuComponent
import com.example.todoapp.ui.components.TextFieldComponent
import com.example.todoapp.ui.theme.Typography
import org.threeten.bp.LocalDate
import org.threeten.bp.LocalTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomSheetComponent(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
    placeholder: String = "",
    buttonText: String = "Confirm",
    initialText: String? = null,
    categories: List<TaskCategoryModel>,
    taskViewModel: TaskViewModel,
    selectedDate: LocalDate = LocalDate.now(),
) {
    var expanded by remember { mutableStateOf(false) }
    var task by remember { mutableStateOf(initialText) }
    var selectedCategory by remember { mutableStateOf<String?>(null) }
    var details by remember { mutableStateOf<String?>(null) }
    var isDetailVisible by remember { mutableStateOf(false) }
    var isDateVisible by remember { mutableStateOf(false) }
    var selectedTime by remember { mutableStateOf<LocalTime?>(null) }
    var selectedDateComponent by remember { mutableStateOf(selectedDate) }

    val showDatePicker by taskViewModel.showDatePicker.collectAsState()
    val temporaryDate by taskViewModel.temporaryDate.collectAsState(null)

    val taskFocusRequester = remember { FocusRequester() }
    val detailsFocusRequester = remember { FocusRequester() }
    val invisibleFocusRequester = remember { FocusRequester() }

    var isTaskFocused by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        taskFocusRequester.requestFocus()
        taskViewModel.resetTemporaryDateTime()
    }

    // Obtener el contexto en el composable usando `LocalContext`
    val context = LocalContext.current

    fun regainFocus() {
        if (isTaskFocused) {
            invisibleFocusRequester.requestFocus()
            taskFocusRequester.requestFocus()
        } else {
            invisibleFocusRequester.requestFocus()
            detailsFocusRequester.requestFocus()
        }
    }

    fun cleanFields() {
        task = ""
        details = null
        selectedCategory = null
        selectedTime = null
        isDetailVisible = false
        taskViewModel.resetTemporaryDateTime()
    }

    fun handleConfirm() {
        if (!task.isNullOrEmpty()) {
            val taskModel = TaskModel(
                task = task!!,
                details = details,
                categoryId = selectedCategory,
                startDate = temporaryDate ?: selectedDate,
                time = selectedTime
            )

            taskViewModel.onTaskCreated(taskModel, context)
            cleanFields()
            onConfirm()
            taskViewModel.resetTemporaryDateTime()
        }
    }

    val textFieldColors = TextFieldDefaults.colors(
        focusedIndicatorColor = Color.Transparent,
        unfocusedIndicatorColor = Color.Transparent,
        cursorColor = colorScheme.onSurface.copy(alpha = 0.5f),
        focusedContainerColor = Color.Transparent,
        unfocusedContainerColor = Color.Transparent,
    )

    ModalBottomSheet(
        onDismissRequest = {
            cleanFields()
            onDismiss()
        },
        dragHandle = null
    ) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp)
                .align(Alignment.CenterHorizontally)
        ) {
            TextField(
                value = task ?: "",
                onValueChange = { task = it },
                placeholder = { Text(text = placeholder) },
                singleLine = true,
                keyboardOptions = KeyboardOptions.Default.copy(
                    imeAction = ImeAction.Done
                ),
                colors = textFieldColors,
                keyboardActions = KeyboardActions(onDone = { handleConfirm() }),
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(taskFocusRequester)
                    .onFocusChanged { isTaskFocused = it.isFocused }
            )
            if (isDetailVisible) {
                TextField(
                    value = details ?: "",
                    textStyle = Typography.bodyMedium,
                    onValueChange = { details = it },
                    placeholder = { Text(text = "Detalles", style = Typography.bodyMedium) },
                    colors = textFieldColors,
                    modifier = Modifier.focusRequester(detailsFocusRequester)
                )
            }

            // TextField invisible para que se pueda recuperar el foco y mostrar el teclado despues de
            // interactuar con el DatePickerDialog
            TextField(
                value = "",
                onValueChange = {},
                modifier = Modifier
                    .size(1.dp)
                    .padding(0.dp)
                    .focusRequester(invisibleFocusRequester),
                singleLine = true
            )

            if (isDateVisible) {
                val formattedDate = formatDate(selectedDateComponent)
                val formattedTime = selectedTime?.let { formatTime(it) }
                val displayText = "$formattedDate${formattedTime?.let { ", $it" } ?: ""}"
                TextFieldComponent(
                    value = displayText,
                    onValueChange = {},
                    enabled = false,
                    modifier = Modifier
                        .wrapContentWidth()
                        .padding(start = 15.dp)
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start
            ) {
                Button(
                    onClick = {
                        expanded = true
                    },
                    modifier = Modifier.padding(4.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = colorScheme.primaryContainer
                    )
                ) {
                    Text(
                        text = categories.find { it.id === selectedCategory }?.category
                            ?: "Sin Categoría"
                    )
                }

                DropdownMenuComponent(
                    properties = PopupProperties(focusable = false),
                    isDropDownExpanded = expanded,
                    onDismissRequest = { expanded = false },
                    items = categories.map { it.category },
                    defaultText = "Sin categoría",
                    onItemSelected = { category ->
                        selectedCategory = categories.find { it.category == category }?.id
                        expanded = false
                    }
                )

                IconButton(onClick = { isDetailVisible = true }) {
                    Icon(
                        painter = painterResource(R.drawable.ic_notes),
                        contentDescription = "Details",
                        tint = colorScheme.inverseSurface
                    )
                }

                IconButton(onClick = {
                    taskViewModel.onShowDateDialogClick()
                }) {
                    Icon(
                        painter = painterResource(R.drawable.ic_access_time),
                        contentDescription = "Select Date",
                        tint = colorScheme.inverseSurface
                    )
                }

                Spacer(modifier = Modifier.weight(1f))

                Button(
                    onClick = { handleConfirm() },
                    enabled = task?.isNotEmpty() ?: false,
                    modifier = Modifier.widthIn(min = 100.dp, max = 200.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = colorScheme.primaryContainer
                    )
                ) {
                    Text(text = buttonText)
                }
            }
        }
    }

    if (showDatePicker) {
        DatePickerDialogComponent(
            initialDate = selectedDate,
            initialTime = selectedTime,
            onDateSelected = { date ->
                taskViewModel.setTemporaryDate(date)
            },
            onTimeSelected = { time ->
                taskViewModel.setTemporaryTime(time)
            },
            onDismiss = {
                taskViewModel.onHideDatePicker()
                regainFocus()
            },
            onConfirm = {
                selectedDateComponent = taskViewModel.temporaryDate.value ?: selectedDate
                selectedTime = taskViewModel.temporaryTime.value
                isDateVisible = true
                regainFocus()
            }
        )
    }
}