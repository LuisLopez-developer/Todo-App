package com.example.todoapp.ui.components

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
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.typography
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.todoapp.R
import com.example.todoapp.taskcategory.ui.TaskCategoryUiState
import com.example.todoapp.taskcategory.ui.TaskCategoryViewModel
import com.example.todoapp.taskcategory.ui.model.TaskCategoryModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomSheetComponent(
    showSheet: Boolean,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit,
    placeholder: String = "",
    buttonText: String = "Confirm",
    initialText: String = "",
    taskCategoryViewModel: TaskCategoryViewModel // Agregado para acceder a las categorías
) {
    var inputText by remember { mutableStateOf(initialText) }
    val focusRequester = remember { FocusRequester() }
    val coroutineScope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    // Estado para manejar la visibilidad del menú desplegable
    var expanded by remember { mutableStateOf(false) }
    var selectedCategory by remember { mutableStateOf<TaskCategoryModel?>(null) }

    // Observa el estado de UI de las categorías desde el ViewModel
    val uiState by taskCategoryViewModel.uiState.collectAsState(TaskCategoryUiState.Loading)

    // Solicita el enfoque al abrir el bottom sheet
    LaunchedEffect(showSheet) {
        if (showSheet) {
            focusRequester.requestFocus()
        }
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
                    keyboardActions = KeyboardActions(onDone = {
                        onConfirm(inputText)
                        inputText = ""
                        coroutineScope.launch { sheetState.hide() }
                    }),
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusRequester(focusRequester)
                )
                Spacer(modifier = Modifier.size(8.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Start
                ) {
                    // Botón de Categoría con DropdownMenu
                    Button(
                        onClick = { expanded = true }, // Mostrar el menú desplegable
                        modifier = Modifier.padding(4.dp)
                    ) {
                        Text(text = selectedCategory?.category ?: "Categoría")
                    }

                    // Menú desplegable
                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        when (uiState) {
                            is TaskCategoryUiState.Success -> {
                                val categories = (uiState as TaskCategoryUiState.Success).categories
                                categories.forEach { category ->
                                    DropdownMenuItem(
                                        text = { Text(category.category) },
                                        onClick = {
                                            selectedCategory = category
                                            expanded = false // Cierra el menú al seleccionar
                                        }
                                    )
                                }
                            }
                            else -> {
                                Text("Loading...")
                            }
                        }
                    }

                    // IconButtons
                    IconButton(onClick = { }) {
                        Icon(
                            painter = painterResource(R.drawable.ic_notes),
                            contentDescription = "Details",
                            tint = colorScheme.inverseSurface
                        )
                    }

                    IconButton(onClick = { }) {
                        Icon(
                            painter = painterResource(R.drawable.ic_access_time),
                            contentDescription = "Select Date",
                            tint = colorScheme.inverseSurface
                        )
                    }

                    // Spacer to push the Button to the end
                    Spacer(modifier = Modifier.weight(1f))

                    // Adjusted Button
                    Button(
                        onClick = {
                            onConfirm(inputText)
                            inputText = ""
                            coroutineScope.launch { sheetState.hide() }
                        },
                        modifier = Modifier
                            .widthIn(min = 100.dp, max = 200.dp) // Ancho mínimo y máximo para el botón
                    ) {
                        Text(text = buttonText)
                    }
                }
            }
        }
    }
}