package com.example.todoapp.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.shapes
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.todoapp.R
import com.example.todoapp.addtasks.ui.utils.formatTime
import org.threeten.bp.LocalDate
import org.threeten.bp.LocalTime

@Composable
fun DatePickerDialogComponent(
    initialDate: LocalDate,
    initialTime: LocalTime? = null,  // Agrega un parámetro para el tiempo temporal
    onDateSelected: (LocalDate) -> Unit,
    onTimeSelected: (LocalTime?) -> Unit,  // Agrega un parámetro para la selección de tiempo
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
) {
    // Estado para manejar la visibilidad del TimePickerDialog
    val showTimePickerDialog = remember { mutableStateOf(false) }
    var temporaryTime by remember { mutableStateOf(initialTime) }

    // Mostrar el diálogo del TimePicker si el estado es verdadero
    if (showTimePickerDialog.value) {
        TimePickerDialogComponent(
            initialTime = temporaryTime,
            onTimeSelected = { time ->
                temporaryTime = time
                showTimePickerDialog.value = false
            },
            onDismiss = { showTimePickerDialog.value = false }
        )
    }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = shapes.extraLarge,
            modifier = Modifier
                .background(Color.Transparent),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(modifier = Modifier.background(colorScheme.surface)) {
                CalendarComponent(
                    modifier = Modifier.padding(20.dp),
                    initialDate = initialDate,
                    onDateSelected = { date -> onDateSelected(date) }
                )
                HorizontalDivider(thickness = 2.dp)
                TextButton(
                    onClick = {
                        showTimePickerDialog.value = true
                    },  // Muestra el diálogo de selección de hora
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Start
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_access_time),
                            contentDescription = "Icono para agregar hora",
                            tint = colorScheme.onBackground,
                            modifier = Modifier.padding(start = 2.dp, end = 6.dp)
                        )

                        if (temporaryTime != null) {
                            val formattedTime = formatTime(temporaryTime!!)
                            TextFieldWithButtonComponent(
                                text = formattedTime,
                                onIconClick = {
                                    temporaryTime = null
                                }
                            )
                        } else {
                            Text(
                                text = "Agregar Hora",
                                color = colorScheme.onBackground,
                                style = typography.bodyMedium
                            )
                        }
                    }
                }
                HorizontalDivider(thickness = 2.dp)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text(text = "Cancelar")
                    }
                    TextButton(onClick = {
                        onTimeSelected(temporaryTime)
                        onConfirm()
                        onDismiss()
                    }) {
                        Text(text = "Aceptar")
                    }
                }
            }
        }
    }
}