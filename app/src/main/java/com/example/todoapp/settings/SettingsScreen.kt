package com.example.todoapp.settings

import android.app.Activity
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import com.example.todoapp.settings.ui.SettingsViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.common.api.ApiException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun SettingsScreen(
    settingsViewModel: SettingsViewModel,
    googleSignInClient: GoogleSignInClient,
) {
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current as Activity

    LaunchedEffect(Unit) {
        settingsViewModel.checkUser()
    }

    val signInLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        Log.e("SettingsScreen", "signInLauncher: $result")
        if (result.resultCode == Activity.RESULT_OK) {
            val account = GoogleSignIn.getLastSignedInAccount(context)
            handleSignInResult(account, settingsViewModel, coroutineScope)
        } else {
            Log.e("SettingsScreen", "signInLauncher: Canceled or failed")
            // Handle the case where the sign-in was canceled or failed
        }
    }

    Column {
        settingsViewModel.user?.let { user ->
            Text(text = "Hello, ${user.name}")
            Log.e("SettingsScreen", "User signed in: $user")
        } ?: run {
            Button(onClick = {
                val signInIntent = googleSignInClient.signInIntent
                Log.e("SettingsScreen", "Launching sign in intent")
                signInLauncher.launch(signInIntent)
            }) {
                Text("Sign in with Google")
            }
        }
    }
}

fun handleSignInResult(account: GoogleSignInAccount?, settingsViewModel: SettingsViewModel, coroutineScope: CoroutineScope) {
    Log.e("SettingsScreen", "handleSignInResult: $account")
    account?.idToken?.let { idToken ->
        Log.e("SettingsScreen", "handleSignInResult: ${account.displayName}")
        Log.e("SettingsScreen", "handleSignInResult: ${account.email}")
        coroutineScope.launch {
            Log.e("SettingsScreen", "handleSignInResult: $idToken")
            settingsViewModel.signInWithGoogle(idToken)
        }
    }
}