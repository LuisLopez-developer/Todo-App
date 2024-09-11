package com.example.todoapp.ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import com.example.todoapp.R

@Composable
fun TextFieldWithButtonComponent(
    modifier: Modifier = Modifier,
    text: String,
    onValueChange: (String) -> Unit = {},
    enabled: Boolean = false,
    readOnly: Boolean = false,
    textStyle: TextStyle = MaterialTheme.typography.labelSmall,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    onIconClick: () -> Unit = {}, // Funcionalidad del botón de "X"
) {

    TextFieldComponent(
        modifier = Modifier
            .wrapContentWidth()
            .padding(start = 6.dp),
        value = text,
        onValueChange = onValueChange,
        enabled = enabled,
        readOnly = readOnly,
        textStyle = textStyle,
        leadingIcon = leadingIcon,
        trailingIcon = {
            Row {
                if (trailingIcon != null) trailingIcon()
                IconButton(
                    onClick = onIconClick,
                    modifier = Modifier.size(24.dp) // Reduce el tamaño del botón
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_close),
                        contentDescription = "Clear text",
                        modifier = Modifier.size(16.dp) // Reduce el tamaño del ícono
                    )
                }

            }
        },
        cornerRadius = 10.dp,
        borderWidth = 1.dp,
        borderColor = colorScheme.inverseSurface
    )
}