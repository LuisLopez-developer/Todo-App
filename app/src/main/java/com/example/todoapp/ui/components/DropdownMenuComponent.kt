package com.example.todoapp.ui.components

import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.window.PopupProperties

@Composable
fun DropdownMenuComponent(
    isDropDownExpanded: Boolean,
    onDismissRequest: () -> Unit,
    items: List<String>,
    defaultText: String = "Sin Información",
    onItemSelected: (String?) -> Unit,
    properties: PopupProperties = PopupProperties(focusable = true)
) {
    DropdownMenu(
        expanded = isDropDownExpanded,
        onDismissRequest = onDismissRequest,
        properties = properties
    ) {
        // Opción para el valor predeterminado
        DropdownMenuItem(
            text = { Text(text = defaultText) },
            onClick = {
                onItemSelected(null)
            }
        )

        // Listar los elementos disponibles
        items.forEach { item ->
            DropdownMenuItem(
                text = { Text(text = item) },
                onClick = {
                    onItemSelected(item)
                }
            )
        }
    }
}

