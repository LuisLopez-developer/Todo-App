package com.example.todoapp.addtasks.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.example.todoapp.R
import com.example.todoapp.ui.components.TextFieldComponent
import com.example.todoapp.ui.theme.Typography


@Composable
fun EditTaskScreen(taskViewModel: TaskViewModel, id: Int) {
    var taskText by remember { mutableStateOf("") }
    var taskDetail by remember { mutableStateOf("") }
    Column {
        TextField(
            value = taskText,
            textStyle = Typography.bodyLarge,
            onValueChange = { taskText = it },
            placeholder = { Text(text = "Editar tarea", style = Typography.bodyLarge) },
            colors = TextFieldDefaults.colors(
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                cursorColor = colorScheme.onSurface.copy(alpha = 0.5f),
                focusedContainerColor = colorScheme.errorContainer,
                unfocusedContainerColor = colorScheme.errorContainer,
            ),
            modifier = Modifier
                .fillMaxWidth(),
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
            onValueChange = { taskDetail = it },
            placeholder = "Agregar detalles",
            modifier = Modifier
                .fillMaxWidth()
                .background(colorScheme.secondaryContainer)
                .padding(horizontal = 7.dp, vertical = 7.dp),
        )


    }

}
