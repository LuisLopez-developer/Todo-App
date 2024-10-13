package com.example.todoapp.alarm.ui

import android.content.Context
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.example.todoapp.R
import com.example.todoapp.ui.components.CardComponent
import com.example.todoapp.ui.layouts.SharedViewModel

@Composable
fun AlarmScreen(
    sharedViewModel: SharedViewModel,

) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    val alarmPermissionGranted by sharedViewModel.alarmPermissionGranted.collectAsState()
    val postNotificationPermissionGranted by sharedViewModel.postNotificationPermissionGranted.collectAsState()

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

    AlarmContent(
        sharedViewModel,
        alarmPermissionGranted,
        postNotificationPermissionGranted,
        context
    )
}

@Composable
fun AlarmContent(
    sharedViewModel: SharedViewModel,
    alarmPermissionGranted: Boolean,
    postNotificationPermissionGranted: Boolean,
    context: Context,
) {
    ConfigTopBar(sharedViewModel)
    Container(
        sharedViewModel,
        alarmPermissionGranted,
        postNotificationPermissionGranted,
        context
    )
}

@Composable
fun ConfigTopBar(sharedViewModel: SharedViewModel) {
    sharedViewModel.topBarTitle.value = stringResource(R.string.alerts_and_notifications)
}

@Composable
fun Container(
    sharedViewModel: SharedViewModel,
    alarmPermissionGranted: Boolean,
    postNotificationPermissionGranted: Boolean,
    context: Context,
) {
    LazyColumn(
        Modifier
            .padding(15.dp)
            .fillMaxSize()
    ) {
        item {
            CardComponent(
                title = stringResource(R.string.alarm_title),
                onClickText = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable {
                            sharedViewModel.requestExactAlarmPermission()
                        }) {
                        if (!alarmPermissionGranted) {
                            Text(
                                text = stringResource(R.string.allow),
                                style = MaterialTheme.typography.bodySmall,
                                color = colorScheme.tertiaryContainer
                            )
                            Icon(
                                painterResource(R.drawable.ic_navigate_next),
                                modifier = Modifier.size(16.dp),
                                contentDescription = null,
                                tint = colorScheme.tertiaryContainer
                            )
                        } else {
                            Icon(
                                painterResource(R.drawable.ic_outline_check_circle),
                                contentDescription = stringResource(R.string.ic_check_circle),
                                tint = colorScheme.tertiaryContainer
                            )
                        }
                    }
                }, description = stringResource(R.string.alarm_description),
                icon = {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_outline_access_alarm),
                        contentDescription = stringResource(R.string.ic_alarm_access),
                        tint = colorScheme.tertiaryContainer
                    )
                }
            )

        }
        item {
            Spacer(modifier = Modifier.padding(10.dp))
            CardComponent(
                title = stringResource(R.string.notifications_title),
                onClickText = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable {
                            sharedViewModel.openAppSettings(context)
                        }) {
                        if (!postNotificationPermissionGranted) {
                            Text(
                                text = stringResource(R.string.allow),
                                style = MaterialTheme.typography.bodySmall,
                                color = colorScheme.tertiaryContainer
                            )
                            Icon(
                                painterResource(R.drawable.ic_navigate_next),
                                modifier = Modifier.size(16.dp),
                                contentDescription = null,
                                tint = colorScheme.tertiaryContainer
                            )
                        } else {
                            Icon(
                                painterResource(R.drawable.ic_outline_check_circle),
                                contentDescription = stringResource(R.string.ic_check_circle),
                                tint = colorScheme.tertiaryContainer
                            )
                        }
                    }
                }, description = stringResource(R.string.notifications_description),
                icon = {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_outline_notifications),
                        contentDescription = stringResource(R.string.ic_notifications),
                        tint = colorScheme.tertiaryContainer
                    )
                }
            )
        }
    }
}