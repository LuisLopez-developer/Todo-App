package com.example.todoapp.alarm.ui.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.example.todoapp.R
import com.example.todoapp.ui.icon.IconCheck
import com.example.todoapp.ui.layouts.SharedViewModel

@Composable
fun PermissionDialog(
    onDismiss: () -> Unit,
    sharedViewModel: SharedViewModel,
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    val postNotificationPermissionGranted by sharedViewModel.postNotificationPermissionGranted.collectAsState()
    val alarmPermissionGranted by sharedViewModel.alarmPermissionGranted.collectAsState()

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                sharedViewModel.checkPermissions()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    Dialog(onDismissRequest = { }) {
        // Ícono de cierre
        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Box {
                // imagen principal
                Image(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(colorScheme.primaryContainer.copy(0.7f)),
                    painter = painterResource(id = R.drawable.notification),
                    contentDescription = "Alarm"
                )

                // Botón de cierre
                Image(
                    painter = painterResource(id = R.drawable.ic_close),
                    contentDescription = stringResource(R.string.ic_close),
                    modifier = Modifier
                        .size(24.dp)
                        .clickable { onDismiss() }
                        .align(Alignment.TopEnd),
                    colorFilter = ColorFilter.tint(colorScheme.onPrimaryContainer),
                )
            }

            // Contenido
            Column(Modifier.background(colorScheme.surfaceContainerLowest)) {
                Text(
                    text = stringResource(R.string.permission_rationale),
                    modifier = Modifier.padding(16.dp)
                )
                Row(Modifier.padding(16.dp)) {
                    Text(
                        text = stringResource(R.string.enable_notifications),
                        Modifier.weight(1f),
                        style = typography.bodyMedium
                    )
                    Box(Modifier.clickable { sharedViewModel.openAppSettings(context) }) {
                        if (!postNotificationPermissionGranted) {
                            Text(
                                text = stringResource(R.string.adjust_settings),
                                color = colorScheme.tertiary,
                            )
                        } else {
                            IconCheck()
                        }
                    }
                }
                Row(Modifier.padding(16.dp)) {
                    Text(
                        text = stringResource(R.string.enable_alarm),
                        Modifier.weight(1f),
                        style = typography.bodyMedium
                    )
                    Box(modifier = Modifier.clickable { sharedViewModel.requestExactAlarmPermission() }) { }
                    if (!alarmPermissionGranted) {
                        Text(
                            text = stringResource(R.string.adjust_settings),
                            color = colorScheme.tertiary
                        )
                    } else {
                        IconCheck()
                    }
                }
            }
        }
    }
}