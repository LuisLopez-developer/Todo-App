package com.example.todoapp.ui.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
    Box(
        modifier = modifier
            .wrapContentWidth()
            .border(
                width = 1.dp,
                color = Color.Gray,
                shape = RoundedCornerShape(8.dp) // Borde redondeado
            )
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
            }
        )
    }
}
