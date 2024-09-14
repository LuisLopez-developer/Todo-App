package com.example.todoapp.addtasks.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.example.todoapp.R
import com.example.todoapp.addtasks.ui.TaskViewModel
import com.example.todoapp.addtasks.ui.utils.formatDate
import com.example.todoapp.addtasks.ui.utils.formatTime
import com.example.todoapp.ui.components.DatePickerDialogComponent
import com.example.todoapp.ui.components.DropdownMenuComponent
import com.example.todoapp.ui.components.TextFieldComponent
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
    categories: List<String>,
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

    val showDatePicker by taskViewModel.showDatePicker.collectAsState()
    val showTimePicker by taskViewModel.showTimePicker.collectAsState()


    // Función auxiliar para manejar la confirmación y el reinicio del estado
    fun cleanFields() {
        task = ""
        details = null
        selectedCategory = null
        selectedTime = null
        isDetailVisible = false
    }

    fun handleConfirm() {
        if (!task.isNullOrEmpty()) {
            taskViewModel.onTaskCreated(
                task = task!!,
                details = details,
                category = selectedCategory,
                startDate = taskViewModel.temporaryDate.value ?: selectedDate,
                time = selectedTime
            )
            cleanFields()
            onConfirm()
        }

    }

    ModalBottomSheet(
        onDismissRequest = { onDismiss() }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .imePadding()
                .background(colorScheme.background)
                .padding(16.dp)
                .align(Alignment.CenterHorizontally),
        ) {
            TextField(
                value = task ?: "",
                onValueChange = { task = it },
                placeholder = { Text(text = placeholder) },
                singleLine = true,
                keyboardOptions = KeyboardOptions.Default.copy(
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(onDone = { handleConfirm() }),
                modifier = Modifier
                    .fillMaxWidth()
            )
            if (isDetailVisible) {
                TextField(
                    value = details ?: "",
                    onValueChange = { details = it },
                    placeholder = { Text(text = "Detalles") }
                )
            }

            if (isDateVisible) {
                val formattedDate = formatDate(taskViewModel.temporaryDate.value ?: selectedDate)
                val formattedTime = selectedTime?.let { formatTime(it) }
                val displayText = "$formattedDate${formattedTime?.let { ", $it" } ?: ""}"
                TextFieldComponent(
                    value = displayText,
                    onValueChange = {},
                    enabled = false,
                    modifier = Modifier
                        .wrapContentWidth()
                )
            }

            Spacer(modifier = Modifier.size(8.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start
            ) {
                // Botón de Categoría con el nuevo DropdownMenu genérico
                Button(
                    onClick = { expanded = true },
                    modifier = Modifier.padding(4.dp)
                ) {
                    Text(text = selectedCategory ?: "Categoría")
                }

                // Menú desplegable genérico
                DropdownMenuComponent(
                    isDropDownExpanded = expanded,
                    onDismissRequest = { expanded = false },
                    items = categories,
                    defaultText = "Sin categoría",
                    onItemSelected = { category ->
                        selectedCategory = category
                        expanded = false
                    }
                )

                // IconButtons
                IconButton(onClick = { isDetailVisible = true }) {
                    Icon(
                        painter = painterResource(R.drawable.ic_notes),
                        contentDescription = "Details",
                        tint = colorScheme.inverseSurface
                    )
                }

                IconButton(onClick = { taskViewModel.onShowDateDialogClick() }) {
                    Icon(
                        painter = painterResource(R.drawable.ic_access_time),
                        contentDescription = "Select Date",
                        tint = colorScheme.inverseSurface
                    )
                }

                Spacer(modifier = Modifier.weight(1f))

                Button(
                    onClick = { handleConfirm() },
                    modifier = Modifier
                        .widthIn(
                            min = 100.dp,
                            max = 200.dp
                        )
                ) {
                    Text(text = buttonText)
                }
            }
        }
    }

    // Mostrar DatePickerDialog
    if (showDatePicker) {
        DatePickerDialogComponent(
            initialDate = selectedDate,
            temporaryTime = taskViewModel.temporaryTime.collectAsState().value,
            onDateSelected = { date ->
                taskViewModel.setTemporaryDate(date)
            },
            onTimeSelected = { time ->
                taskViewModel.setTemporaryTime(time)
            },
            onDismiss = { taskViewModel.onHideDatePicker() },
            onConfirm = {
                selectedTime = taskViewModel.temporaryTime.value
                isDateVisible = true
            }
        )
    }

}