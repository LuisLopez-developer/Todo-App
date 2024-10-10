package com.example.todoapp.settings.drive.ui

import android.app.Activity
import android.content.Context
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.todoapp.R
import com.example.todoapp.settings.auth.ui.AuthViewModel
import com.example.todoapp.ui.components.AlertDialogComponent
import com.example.todoapp.ui.components.CardComponent
import com.example.todoapp.ui.layouts.SharedViewModel

@Composable
fun DriveScreen(
    userId: String,
    driveViewModel: DriveViewModel,
    sharedViewModel: SharedViewModel,
    authViewModel: AuthViewModel,
) {
    // Establecer el ID de la tarea en el ViewModel
    LaunchedEffect(userId) {
        driveViewModel.setUserId(userId)
    }

    val syncAutoChecked by driveViewModel.syncAutoChecked.collectAsState()
    val expandedAlert by driveViewModel.expandedAlert.collectAsState()

    val context = LocalContext.current

    Content(sharedViewModel, driveViewModel, syncAutoChecked, authViewModel, context, expandedAlert)

}

@Composable
fun Content(
    sharedViewModel: SharedViewModel,
    driveViewModel: DriveViewModel,
    syncAutoChecked: Boolean,
    authViewModel: AuthViewModel,
    context: Context,
    expandedAlert: Boolean,
) {
    ConfigTopBar(sharedViewModel)
    Container(driveViewModel, syncAutoChecked, authViewModel, context, expandedAlert)
}

@Composable
fun ConfigTopBar(sharedViewModel: SharedViewModel) {
    val title = stringResource(R.string.sync_cloud)

    LaunchedEffect(Unit) {
        sharedViewModel.topBarTitle.value = title
    }
}

data class CardData(
    val icon: Painter,
    val contentDescription: String,
    val title: String,
    val description: String,
    val isSyncAuto: Boolean = false, // Para identificar si lleva un toggle
    val textClick: String? = null, // Texto para clickear
    val onClick: () -> Unit = {},    // Función para manejar el clic
    val toggleChecked: Boolean = false, // Estado del toggle si aplica
    val onToggleChange: ((Boolean) -> Unit)? = null, // Función para manejar el cambio del toggle si aplica
)

@Composable
fun cardItems(
    driveViewModel: DriveViewModel,
    syncAutoChecked: Boolean,
    authViewModel: AuthViewModel,
    context: Context,
) = listOf(
    CardData(
        icon = painterResource(R.drawable.ic_cloud_sync),
        contentDescription = stringResource(R.string.ic_sync_cloud),
        title = stringResource(R.string.sync_cloud),
        description = stringResource(R.string.sync_cloud_description),
        onClick = {
            authViewModel.requestDriveAuthorization(activity = (context as Activity), onResult = {
                driveViewModel.syncDataWith(it)
            })
        },
        textClick = stringResource(R.string.btn_sync)
    ),
    CardData(
        icon = painterResource(R.drawable.ic_cloud_download),
        contentDescription = stringResource(R.string.ic_cloud_download),
        title = stringResource(R.string.sync_cloud_download),
        description = stringResource(R.string.sync_cloud_download_description),
        onClick = {
            authViewModel.requestDriveAuthorization(activity = (context as Activity), onResult = {
                driveViewModel.syncDataFrom(it)
            })
        },
        textClick = stringResource(R.string.btn_cloud_download)
    ),
    CardData(
        icon = painterResource(R.drawable.ic_autorenew),
        contentDescription = stringResource(R.string.ic_autoRenew),
        title = stringResource(R.string.sync_autoRenew),
        description = stringResource(R.string.sync_autoRenew_description),
        isSyncAuto = true,
        toggleChecked = syncAutoChecked,
        onToggleChange = { driveViewModel.onSyncAutoChecked() }
    )
)

@Composable
fun Container(
    driveViewModel: DriveViewModel,
    syncAutoChecked: Boolean,
    authViewModel: AuthViewModel,
    context: Context,
    expandedAlert: Boolean,
) {

    val cardItems = cardItems(driveViewModel, syncAutoChecked, authViewModel, context)

    LazyColumn(modifier = Modifier.padding(15.dp)) {
        items(cardItems) { card ->
            CardComponent(
                icon = {
                    Icon(
                        painter = card.icon,
                        contentDescription = card.contentDescription,
                        tint = colorScheme.tertiaryContainer,
                    )
                },
                title = card.title,
                description = card.description,
                toggle = if (card.isSyncAuto) {
                    {
                        Switch(
                            checked = card.toggleChecked,
                            onCheckedChange = card.onToggleChange,
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = colorScheme.surfaceContainerLowest,
                                checkedTrackColor = colorScheme.tertiaryContainer.copy(alpha = 0.9f),
                                checkedBorderColor = Color.Transparent,
                                uncheckedThumbColor = colorScheme.surfaceContainerLowest,
                                uncheckedTrackColor = colorScheme.outline.copy(alpha = 0.3f),
                                uncheckedBorderColor = Color.Transparent
                            )
                        )
                    }
                } else null,
                onClickText = {
                    if (!card.isSyncAuto) {
                        Text(
                            text = card.textClick ?: "",
                            style = MaterialTheme.typography.bodyMedium,
                            color = colorScheme.tertiary,
                            modifier = Modifier.clickable { card.onClick() }
                        )
                    }
                }
            )
            Spacer(modifier = Modifier.height(20.dp))
        }
        item {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stringResource(R.string.delete_all),
                    color = colorScheme.error,
                    modifier = Modifier.clickable { driveViewModel.onExpandedAlert() }
                )
            }
        }
    }

    if (expandedAlert) {
        AlertDialogComponent(
            title = stringResource(R.string.confirm_delete_title),
            message = stringResource(R.string.confirm_delete_message),
            onDismissRequest = { driveViewModel.onExpandedAlert() },
            onConfirm = {
                authViewModel.requestDriveAuthorization(
                    activity = (context as Activity),
                    onResult = {
                        driveViewModel.clearAppDataFromGoogleDrive(it)
                    }
                )
            }
        )
    }
}