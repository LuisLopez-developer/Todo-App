package com.example.todoapp.settings

import android.app.Activity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import com.example.todoapp.settings.auth.doGoogleSignIn
import com.example.todoapp.settings.ui.SettingsViewModel

@Composable
fun SettingsScreen(settingsViewModel: SettingsViewModel) {
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current as Activity

    val startAddAccountIntentLauncher =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.StartActivityForResult()) {
            // Una vez agregada la cuenta, inicie sesión nuevamente.
            doGoogleSignIn(settingsViewModel, coroutineScope, context, null)
        }

    Column {
        settingsViewModel.user?.let { user ->
            Text(text = "Hola, ${user.name}")
            Button(onClick = {
                settingsViewModel.syncTasks()
            }) {
                Text("Sincronizar tareas con Firebase")
            }
            Button(onClick = {
                settingsViewModel.syncTasksFromFirebase()
            }) {
                Text("Sincronizar tareas desde Firebase")
            }

        } ?: run {
            Button(onClick = {
                doGoogleSignIn(
                    settingsViewModel,
                    coroutineScope,
                    context,
                    startAddAccountIntentLauncher
                )
            }) {
                Text("Iniciar sesión con Google")
            }
        }
    }
}