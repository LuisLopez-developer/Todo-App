package com.example.todoapp.addtasks.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxColors
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp

@Composable
fun TaskItemComponent(
    text: String,
    onClick: () -> Unit,
    onLongPress: () -> Unit,
    checked: Boolean,
    onCheckBoxChange: (Boolean) -> Unit,
) {
    var isLongPress by remember { mutableStateOf(false) }

    val alpha = if (checked) 0.5f else 1f
    val backgroundColor = colorScheme.surfaceContainerHighest.copy(alpha = alpha)
    val textColor = colorScheme.onSurface.copy(alpha = alpha)
    val checkboxColors = CheckboxColors(
        checkedBoxColor = colorScheme.primaryContainer.copy(alpha = alpha),
        uncheckedBoxColor = colorScheme.surfaceContainerHighest.copy(alpha = alpha),
        disabledCheckedBoxColor = colorScheme.primary.copy(alpha = alpha),
        disabledUncheckedBoxColor = colorScheme.primary.copy(alpha = alpha),
        disabledIndeterminateBoxColor = colorScheme.primary.copy(alpha = alpha),
        checkedBorderColor = colorScheme.primaryContainer.copy(alpha = alpha),
        uncheckedBorderColor = colorScheme.outline.copy(alpha = alpha),
        disabledBorderColor = colorScheme.primary.copy(alpha = alpha),
        disabledUncheckedBorderColor = colorScheme.primary.copy(alpha = alpha),
        disabledIndeterminateBorderColor = colorScheme.primary.copy(alpha = alpha),
        checkedCheckmarkColor = colorScheme.onPrimary.copy(alpha = alpha),
        uncheckedCheckmarkColor = colorScheme.primary.copy(alpha = alpha)
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .pointerInput(Unit) {
                detectTapGestures(
                    onLongPress = {
                        isLongPress = true
                        onLongPress() // Acción en long press
                    },
                    onTap = {
                        if (!isLongPress) {
                            onClick() // Acción en tap
                        }
                    }
                )
            }
            .padding(horizontal = 5.dp, vertical = 5.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .background(color = backgroundColor)
                .padding(6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = checked,
                onCheckedChange = { isChecked ->
                    onCheckBoxChange(isChecked) // Actualiza el estado del Checkbox
                },
                colors = checkboxColors
            )
            Text(
                text = text,
                modifier = Modifier.weight(1f),
                color = textColor,
                textDecoration = if (checked) TextDecoration.LineThrough else TextDecoration.None
            )
        }
    }
}