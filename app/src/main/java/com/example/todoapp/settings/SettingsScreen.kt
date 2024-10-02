package com.example.todoapp.settings

import android.app.Activity
import android.content.Intent
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import com.example.todoapp.settings.auth.doGoogleSignIn
import com.example.todoapp.settings.auth.ui.model.UserModel
import com.example.todoapp.settings.ui.SettingsViewModel
import com.example.todoapp.settings.ui.UserUiState
import kotlinx.coroutines.CoroutineScope

@Composable
fun SettingsScreen(settingsViewModel: SettingsViewModel) {
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current as Activity

    val lifecycle = LocalLifecycleOwner.current.lifecycle

    val startAddAccountIntentLauncher =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.StartActivityForResult()) {
            // Una vez agregada la cuenta, inicie sesión nuevamente.
            doGoogleSignIn(settingsViewModel, coroutineScope, context, null)
        }

    val userUiState by produceState<UserUiState>(
        initialValue = UserUiState.Loading,
        key1 = lifecycle,
        key2 = settingsViewModel
    ){
        lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED){
            settingsViewModel.userUiState.collect { value = it }
        }
    }

    when(userUiState){
        is UserUiState.Success -> {
            Container(
                userModel = (userUiState as UserUiState.Success).user,
                startAddAccountIntentLauncher = startAddAccountIntentLauncher,
                settingsViewModel = settingsViewModel,
                coroutineScope = coroutineScope,
                context = context
            )
        }
        is UserUiState.Empty -> {
            Container(
                userModel = null,
                startAddAccountIntentLauncher = startAddAccountIntentLauncher,
                settingsViewModel = settingsViewModel,
                coroutineScope = coroutineScope,
                context = context
            )
        }
        is UserUiState.Loading -> {
            Text("Cargando...")
        }
        is UserUiState.Error -> {
            Text("Error: ${(userUiState as UserUiState.Error).throwable.message}")
        }
    }
}

@Composable
fun Container(
    userModel: UserModel?,
    startAddAccountIntentLauncher: ManagedActivityResultLauncher<Intent, ActivityResult>?,
    settingsViewModel: SettingsViewModel,
    coroutineScope: CoroutineScope,
    context: Activity
){
    Column {
        if(userModel == null){
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
        } else {

            Text(text = "Hola, ${userModel.name }")

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
        }
    }
}