package com.example.todoapp.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.todoapp.R.string
import kotlinx.coroutines.delay

@Composable
fun AlertDialogComponent(
    onConfirm: () -> Unit,
    title: String = stringResource(id = string.sn),
    message: String = stringResource(id = string.sn),
    confirmText: String = stringResource(id = string.ok),
    dismissText: String = stringResource(id = string.cancel),
    dismissEnabled: Boolean = true,
    confirmEnabled: Boolean = true,
    onDismissRequest: () -> Unit,
    timer: Int = 10,
) {
    var  timerDefault by remember { mutableStateOf(timer) }
    var isConfirmEnabled by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        while (timerDefault > 0) {
            delay(1000L)
            timerDefault--
        }
        isConfirmEnabled = true
    }

    Dialog(onDismissRequest = onDismissRequest) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
            shape = RoundedCornerShape(28.dp),
        ) {
            Box(Modifier.background(colorScheme.surfaceContainerLowest)) {
                Column(Modifier.padding(24.dp)) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .fillMaxWidth()

                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = message,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.CenterHorizontally)
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        if (dismissEnabled) {
                            TextButton(
                                onClick = onDismissRequest,
                            ) {
                                Text(text = dismissText, color = colorScheme.primary)
                            }
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        if (confirmEnabled) {
                            TextButton(
                                onClick = onConfirm,
                                enabled = isConfirmEnabled,
                            ) {
                                Text(
                                    text = if (isConfirmEnabled) confirmText else "($timerDefault) $confirmText",
                                    color = if (isConfirmEnabled) colorScheme.error else colorScheme.error.copy(0.5f)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}