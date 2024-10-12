package com.example.todoapp.alarm.ui.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.zIndex
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.example.todoapp.R
import com.example.todoapp.alarm.ui.AlarmViewModel
import com.example.todoapp.ui.icon.IconCheck

@Composable
fun PermissionDialog(
    onDismiss: () -> Unit,
    alarmViewModel: AlarmViewModel,
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    val postNotificationPermissionGranted by alarmViewModel.postNotificationPermissionGranted.collectAsState()
    val alarmPermissionGranted by alarmViewModel.alarmPermissionGranted.collectAsState()

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                alarmViewModel.canScheduleExactAlarms()
                alarmViewModel.checkNotificationPermission(context)
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    Dialog(onDismissRequest = onDismiss) {
        // Ícono de cierre

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .zIndex(1f)
                .offset(y = (-26).dp)
                .clickable { onDismiss() },
            contentAlignment = Alignment.CenterEnd
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_close), // Icono de cerrar
                contentDescription = stringResource(R.string.ic_close),
                modifier = Modifier
                    .size(24.dp)
                    .background(colorScheme.surfaceContainerLowest.copy(alpha = 0.4f), RoundedCornerShape(50)),
                colorFilter = ColorFilter.tint(colorScheme.onPrimary),
            )
        }

        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(Modifier.background(colorScheme.surfaceContainerLowest)) {

                Image(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(colorScheme.primaryContainer.copy(0.7f)),
                    painter = painterResource(id = R.drawable.notification),
                    contentDescription = "Alarm"
                )
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
                    Box(Modifier.clickable { alarmViewModel.openAppSettings(context) }) {
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
                    Box(modifier = Modifier.clickable { alarmViewModel.requestExactAlarmPermission() }) { }
                    if (alarmPermissionGranted) {
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