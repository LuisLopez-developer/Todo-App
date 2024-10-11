package com.example.todoapp.settings.auth.ui.component

import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.credentials.exceptions.GetCredentialException
import androidx.credentials.exceptions.NoCredentialException
import com.example.todoapp.BuildConfig
import com.example.todoapp.R
import com.example.todoapp.settings.auth.ui.AuthViewModel
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.security.MessageDigest
import java.util.UUID

@Composable
fun SignInWithGoogle(authViewModel: AuthViewModel) {
    SignInWithGoogleButton(authViewModel)
}

@Composable
fun SignInWithGoogleButton(authViewModel: AuthViewModel) {
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    val startAddAccountIntentLauncher =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.StartActivityForResult()) {
            // Una vez agregada la cuenta, inicie sesión nuevamente.
            doGoogleSignIn(authViewModel, coroutineScope, context, null)
        }

    IconButton(onClick = {
        doGoogleSignIn(
            authViewModel,
            coroutineScope,
            context,
            startAddAccountIntentLauncher
        )
    }) {
        Icon(
            painter = painterResource(R.drawable.ic_google),
            contentDescription = "Google",
            tint = Color.Unspecified
        )
    }

}

fun doGoogleSignIn(
    authViewModel: AuthViewModel,
    coroutineScope: CoroutineScope,
    context: Context,
    startAddAccountIntentLauncher: ManagedActivityResultLauncher<Intent, ActivityResult>?,
) {

    if (!authViewModel.isNetworkAvailable()) {
        return
    }

    val credentialManager = CredentialManager.create(context)

    fun getGoogleIdOption(): GetGoogleIdOption {
        val rawNonce = UUID.randomUUID().toString()
        val bytes = rawNonce.toByteArray()
        val md = MessageDigest.getInstance("SHA-256")
        val digest = md.digest(bytes)
        val hashedNonce = digest.fold("") { str, it ->
            str + "%02x".format(it)
        }

        return GetGoogleIdOption.Builder()
            .setFilterByAuthorizedAccounts(false)
            .setServerClientId(BuildConfig.WEB_CLIENT_ID)
            .setAutoSelectEnabled(true)
            .setNonce(hashedNonce)
            .build()
    }

    val googleSignRequest: GetCredentialRequest = GetCredentialRequest.Builder()
        .addCredentialOption(getGoogleIdOption())
        .build()

    coroutineScope.launch {
        try {
            val result: GetCredentialResponse = credentialManager.getCredential(
                request = googleSignRequest,
                context = context,
            )

            authViewModel.handleSignIn(result, context)

        } catch (e: NoCredentialException) {
            e.printStackTrace()
            startAddAccountIntentLauncher?.launch(getAddGoogleAccountIntent())
        } catch (e: GetCredentialCancellationException) {
            Log.e("SettingsScreen", "Error de inicio de sesión", e)
            Toast.makeText(context, "Error de inicio de sesión", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE && e is GetCredentialException) {
                e.printStackTrace()
            } else {
                Log.e("SettingsScreen", "Unexpected error", e)
            }
        }
    }
}

private fun getAddGoogleAccountIntent(): Intent {
    val intent = Intent(Settings.ACTION_ADD_ACCOUNT)
    intent.putExtra(Settings.EXTRA_ACCOUNT_TYPES, arrayOf("com.google"))
    return intent
}