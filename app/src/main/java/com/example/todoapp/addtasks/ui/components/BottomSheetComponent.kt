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
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
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
import com.example.todoapp.ui.components.TimePickerDialogComponent
import kotlinx.coroutines.launch
import org.threeten.bp.LocalDate
import org.threeten.bp.LocalTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomSheetComponent(
    showSheet: Boolean,
    onDismiss: () -> Unit,
    onConfirm: (String, String?, String?, LocalDate, LocalTime?) -> Unit,
    placeholder: String = "",
    buttonText: String = "Confirm",
    initialText: String = "",
    categories: List<String>,
    taskViewModel: TaskViewModel,
    initialDate: LocalDate = LocalDate.now(),
) {
    val focusRequester = remember { FocusRequester() }
    val coroutineScope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    var expanded by remember { mutableStateOf(false) }
    var inputText by remember { mutableStateOf(initialText) }
    var selectedCategory by remember { mutableStateOf<String?>(null) }
    var details by remember { mutableStateOf<String?>(null) }
    var isDetailVisible by remember { mutableStateOf(false) }
    var isDateVisible by remember { mutableStateOf(false) }

    // Estados para fecha y hora
    var selectedDate by remember { mutableStateOf<LocalDate?>(LocalDate.now()) }
    var selectedTime by remember { mutableStateOf<LocalTime?>(null) }

    val showDatePicker by taskViewModel.showDatePicker.collectAsState()
    val showTimePicker by taskViewModel.showTimePicker.collectAsState()

    // Solicita el enfoque al abrir el bottom sheet
    LaunchedEffect(showSheet) {
        if (showSheet) {
            focusRequester.requestFocus()
        }
    }

    // Función auxiliar para manejar la confirmación y el reinicio del estado
    fun handleConfirm() {
        onConfirm(
            inputText,
            selectedCategory,
            details,
            selectedDate ?: LocalDate.now(),
            selectedTime
        )
        inputText = ""
        details = null
        selectedCategory = null
        selectedDate = LocalDate.now()
        selectedTime = null
        isDetailVisible = false
        isDateVisible = false
        coroutineScope.launch { sheetState.hide() }
    }

    if (showSheet) {
        ModalBottomSheet(
            onDismissRequest = { onDismiss() },
            sheetState = sheetState
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
                    value = inputText,
                    onValueChange = { inputText = it },
                    placeholder = { Text(text = placeholder) },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions.Default.copy(
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(onDone = { handleConfirm() }),
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusRequester(focusRequester)
                )
                if (isDetailVisible) {
                    TextField(
                        value = details ?: "",
                        onValueChange = { details = it },
                        placeholder = { Text(text = "Detalles") }
                    )
                }

                if (isDateVisible) {
                    val formattedDate = formatDate(selectedDate ?: LocalDate.now())
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
    }

    // Mostrar DatePickerDialog
    if (showDatePicker) {
        DatePickerDialogComponent(
            initialDate = initialDate,
            taskViewModel = taskViewModel,
            onDismiss = { taskViewModel.onHideDatePicker() },
            onConfirm = {
                selectedDate = taskViewModel.temporaryDate.value ?: LocalDate.now()
                selectedTime = taskViewModel.temporaryTime.value
                taskViewModel.resetTemporaryDateTime()
                isDateVisible = true
            }
        )
    }

    // Mostrar TimePickerDialog
    if (showTimePicker) {
        TimePickerDialogComponent(
            taskViewModel = taskViewModel,
            onDismiss = { taskViewModel.onHideTimePicker() },
        )
    }

}