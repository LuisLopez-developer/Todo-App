package com.example.todoapp.settings.auth.ui

import android.app.Activity
import android.app.PendingIntent
import android.content.Context
import android.content.IntentSender
import android.util.Log
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialResponse
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.todoapp.core.NetWorkService
import com.example.todoapp.settings.auth.domain.HandleSignInUseCase
import com.example.todoapp.utils.Logger
import com.google.android.gms.auth.api.identity.AuthorizationRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.common.api.Scope
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import com.google.api.services.drive.DriveScopes
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.GoogleAuthProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val handleSignInUseCase: HandleSignInUseCase,
    private val netWorkService: NetWorkService
) : ViewModel() {

    fun isNetworkAvailable(): Boolean {
        return netWorkService.getNetworkService()
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

    fun handleSignIn(result: GetCredentialResponse, context: Context) {
        when (val credential = result.credential) {
            is CustomCredential -> {
                if (credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                    try {
                        val googleIdToken =
                            GoogleIdTokenCredential.createFrom(credential.data).idToken

                        val authCredential: AuthCredential = GoogleAuthProvider.getCredential(googleIdToken, null)

                        viewModelScope.launch {
                            handleSignInUseCase(authCredential)

                            requestDriveAuthorization(context as Activity, onResult = {
                                Logger.info("SettingsScreen", "Access token")
                            })
                        }

                    } catch (e: GoogleIdTokenParsingException) {
                        Logger.error(
                            "signInWithGoogle",
                            "Received an invalid google id token response",
                            e
                        )
                    } catch (e: Exception) {
                        Logger.error("signInWithGoogle", "Unexpected error", e)
                    }
                } else {
                    Logger.error("signInWithGoogle", "Unexpected type of credential")
                }
            }

            else -> {
                Logger.error("signInWithGoogle", "Unexpected type of credential")
            }
        }

    }

    companion object {
        const val REQUEST_AUTHORIZE = 1001
    }

}