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
import com.example.todoapp.ui.components.DropdownMenuComponent
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomSheetComponent(
    showSheet: Boolean,
    onDismiss: () -> Unit,
    onConfirm: (String, String?) -> Unit, // Cambiado para incluir la categoría como String en lugar de TaskCategoryModel
    placeholder: String = "",
    buttonText: String = "Confirm",
    initialText: String = "",
    categories: List<String>, // Cambiado para aceptar una lista de String en lugar de usar el ViewModel
) {
    var inputText by remember { mutableStateOf(initialText) }
    val focusRequester = remember { FocusRequester() }
    val coroutineScope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    // Estado para manejar la visibilidad del menú desplegable
    var expanded by remember { mutableStateOf(false) }
    var selectedCategory by remember { mutableStateOf<String?>(null) }

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
                        onConfirm(inputText, selectedCategory) // Pasa ambos parámetros
                        inputText = ""
                        selectedCategory =
                            null // Reiniciar categoría seleccionada después de confirmar
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
                    // Botón de Categoría con el nuevo DropdownMenu genérico
                    Button(
                        onClick = { expanded = true }, // Mostrar el menú desplegable
                        modifier = Modifier.padding(4.dp)
                    ) {
                        Text(text = selectedCategory ?: "Categoría")
                    }

                    // Menú desplegable genérico
                    DropdownMenuComponent(
                        isDropDownExpanded = expanded,
                        onDismissRequest = { expanded = false },
                        items = categories, // Pasa la lista de categorías como String
                        defaultText = "Sin categoría",
                        onItemSelected = { category ->
                            selectedCategory = category
                            expanded = false // Cierra el menú al seleccionar
                        }
                    )

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

                    // Ajustado Button
                    Button(
                        onClick = {
                            onConfirm(inputText, selectedCategory) // Pasa ambos parámetros
                            inputText = ""
                            selectedCategory =
                                null // Reiniciar categoría seleccionada después de confirmar
                            coroutineScope.launch { sheetState.hide() }
                        },
                        modifier = Modifier
                            .widthIn(
                                min = 100.dp,
                                max = 200.dp
                            ) // Ancho mínimo y máximo para el botón
                    ) {
                        Text(text = buttonText)
                    }
                }
            }
        }
    }
}