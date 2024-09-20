package com.example.todoapp.settings.ui

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialResponse
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.todoapp.settings.auth.data.UserEntity
import com.example.todoapp.settings.auth.domain.AddUserCaseUse
import com.example.todoapp.settings.auth.domain.SignInWithGoogleUseCase
import com.example.todoapp.settings.firestore.domain.SyncDataFromFirestoreUseCase
import com.example.todoapp.settings.firestore.domain.SyncDataWithFirebaseUseCase
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val signInWithGoogleUseCase: SignInWithGoogleUseCase,
    private val addUserCaseUse: AddUserCaseUse,
    private val syncDataFromFirestoreUseCase: SyncDataFromFirestoreUseCase,
    private val syncDataWithFirestoreUseCase: SyncDataWithFirebaseUseCase,
) : ViewModel() {
    var user by mutableStateOf<UserEntity?>(null)
        private set

    private fun signInWithGoogle(idToken: String) {
        viewModelScope.launch {
            signInWithGoogleUseCase(idToken, onSuccess = {
                val firebaseUser =  FirebaseAuth.getInstance().currentUser
                firebaseUser?.let {
                    val userEntity =
                        UserEntity(
                            uid = it.uid,
                            name = it.displayName ?: "",
                            email = it.email ?: ""
                        )
                    viewModelScope.launch {
                        addUserCaseUse(userEntity)
                        user = userEntity
                    }
                }
            }, onFailure = {
                Log.e("SettingsViewModel", "signInWithGoogle: ${it.message}")
            })
        }
    }

    fun handleSignIn(result: GetCredentialResponse) {
        when (val credential = result.credential) {
            is CustomCredential -> {

                if (credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                    try {

                        val googleIdTokenCredential = GoogleIdTokenCredential
                            .createFrom(credential.data)

                        val googleIdToken = googleIdTokenCredential.idToken

                        signInWithGoogle(googleIdToken)

                    } catch (e: GoogleIdTokenParsingException) {
                        Log.e("TAG", "Received an invalid google id token response", e)
                    } catch (e: Exception) {
                        Log.e("TAG", "Unexpected error")
                    }
                }
            }

            else -> {
                // Analizar cualquier tipo de credencial personalizada que no sea GoogleIdTokenCredential
                Log.e("TAG", "Unexpected type of credential")
            }
        }
    }

    fun syncTasks() {
        viewModelScope.launch {
            syncDataWithFirestoreUseCase()
        }
    }

    fun syncTasksFromFirebase() {
        viewModelScope.launch {
            syncDataFromFirestoreUseCase()
        }
    }
}