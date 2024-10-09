package com.example.todoapp.settings.auth.ui

import android.app.Activity
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.os.Build
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.result.ActivityResult
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.credentials.exceptions.GetCredentialException
import androidx.credentials.exceptions.NoCredentialException
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.todoapp.BuildConfig
import com.example.todoapp.settings.ui.SettingsViewModel
import com.google.android.gms.auth.api.identity.AuthorizationRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.common.api.Scope
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.api.services.drive.DriveScopes
import kotlinx.coroutines.launch
import java.security.MessageDigest
import java.util.UUID
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor() : ViewModel() {
    private fun getAddGoogleAccountIntent(): Intent {
        val intent = Intent(Settings.ACTION_ADD_ACCOUNT)
        intent.putExtra(Settings.EXTRA_ACCOUNT_TYPES, arrayOf("com.google"))
        return intent
    }

    fun doGoogleSignIn(
        settingsViewModel: SettingsViewModel,
        context: Context,
        startAddAccountIntentLauncher: ManagedActivityResultLauncher<Intent, ActivityResult>?
    ) {
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

        viewModelScope.launch {
            try {
                val result = credentialManager.getCredential(
                    request = googleSignRequest,
                    context = context,
                )
                settingsViewModel.handleSignIn(result)

                requestDriveAuthorization(context as Activity, onResult = {
                    Log.i("SettingsScreen", "Access token: $it")
                })

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

    fun requestDriveAuthorization(activity: Activity, onResult: (String) -> Unit) {
        val requestedScopes = listOf(Scope(DriveScopes.DRIVE_APPDATA))
        val authorizationRequest = AuthorizationRequest.Builder()
            .setRequestedScopes(requestedScopes)
            .build()

        Identity.getAuthorizationClient(activity)
            .authorize(authorizationRequest)
            .addOnSuccessListener { authorizationResult ->
                if (authorizationResult.hasResolution()) {
                    val pendingIntent: PendingIntent? = authorizationResult.pendingIntent
                    try {
                        activity.startIntentSenderForResult(
                            pendingIntent?.intentSender,
                            REQUEST_AUTHORIZE,
                            null,
                            0,
                            0,
                            0,
                            null
                        )
                    } catch (e: IntentSender.SendIntentException) {
                        Log.e("AuthUtils", "Couldn't start Authorization UI: ${e.localizedMessage}")
                    }
                } else {
                    // Devuelve el resultado de la autorización
                    authorizationResult.accessToken?.let { onResult(it) }
                }
            }
            .addOnFailureListener { e ->
                Log.e("AuthUtils", "Failed to authorize", e)
            }
    }

    companion object {
        const val REQUEST_AUTHORIZE = 1001
    }
}