package com.example.todoapp.ui.components

import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@Composable
fun DropdownMenuComponent(
    isDropDownExpanded: Boolean,
    onDismissRequest: () -> Unit,
    items: List<String>,
    defaultText: String = "Sin Información",
    onItemSelected: (String?) -> Unit,
) {
    DropdownMenu(
        expanded = isDropDownExpanded,
        onDismissRequest = onDismissRequest
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

