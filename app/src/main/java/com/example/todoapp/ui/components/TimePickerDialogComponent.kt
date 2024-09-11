package com.example.todoapp.ui.components

import androidx.compose.foundation.background
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.todoapp.addtasks.ui.TaskViewModel
import org.threeten.bp.LocalTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimePickerDialogComponent(
    taskViewModel: TaskViewModel,
    onDismiss: () -> Unit,
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = MaterialTheme.shapes.extraLarge,
            tonalElevation = 6.dp,
            modifier = Modifier
                .background(colorScheme.surface)
        ) {
            AdvancedTimePickerComponent(
                onConfirm = { timePickerState ->
                    taskViewModel.setTemporaryTime(
                        LocalTime.of(
                            timePickerState.hour,
                            timePickerState.minute
                        )
                    )
                    onDismiss()
                },
                onDismiss = {
                    onDismiss()
                }
            )
        }
    }
}
