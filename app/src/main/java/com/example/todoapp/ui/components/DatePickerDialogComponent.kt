package com.example.todoapp.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.shapes
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.todoapp.R
import com.example.todoapp.addtasks.ui.TaskViewModel
import com.example.todoapp.addtasks.ui.utils.formatTime
import org.threeten.bp.LocalDate

@Composable
fun DatePickerDialogComponent(
    taskViewModel: TaskViewModel,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
    initialDate: LocalDate = LocalDate.now(),
) {
    val temporaryTime by taskViewModel.temporaryTime.collectAsState()

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = shapes.extraLarge,
            modifier = Modifier
                .background(colorScheme.surface)
        ) {
            Column {
                CalendarComponent(
                    modifier = Modifier.padding(20.dp),
                    initialDate = initialDate,
                    onDateSelected = { date ->
                        taskViewModel.setTemporaryDate(date)
                    }
                )
                HorizontalDivider(thickness = 2.dp)
                TextButton(
                    onClick = { taskViewModel.onShowTimePicker() },
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
                                    taskViewModel.setTemporaryTime(null)
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