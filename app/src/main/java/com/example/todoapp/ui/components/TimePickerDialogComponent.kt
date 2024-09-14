package com.example.todoapp.ui.components

import androidx.compose.foundation.background
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Surface
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import org.threeten.bp.LocalTime
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimePickerDialogComponent(
    initialTime: LocalTime? = null,
    onTimeSelected: (LocalTime) -> Unit,
    onDismiss: () -> Unit,
) {
    // Convert LocalTime to Calendar for initial time setup
    val initialCalendar = Calendar.getInstance().apply {
        initialTime?.let {
            set(Calendar.HOUR_OF_DAY, it.hour)
            set(Calendar.MINUTE, it.minute)
        }
    }

    val timePickerState = rememberTimePickerState(
        initialHour = initialCalendar.get(Calendar.HOUR_OF_DAY),
        initialMinute = initialCalendar.get(Calendar.MINUTE),
        is24Hour = true,  // Adjust this to your preferred time format
    )

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = MaterialTheme.shapes.extraLarge,
            tonalElevation = 6.dp,
            modifier = Modifier
                .background(colorScheme.surface)
        ) {
            AdvancedTimePickerComponent(
                onConfirm = { state ->
                    val selectedTime = LocalTime.of(state.hour, state.minute)
                    onTimeSelected(selectedTime)
                    onDismiss()
                },
                onDismiss = onDismiss
            )
        }
    }
}