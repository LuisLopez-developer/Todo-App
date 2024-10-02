package com.example.todoapp.settings.ui

import android.util.Log
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialResponse
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.todoapp.settings.auth.data.UserEntity
import com.example.todoapp.settings.auth.domain.AddUserCaseUse
import com.example.todoapp.settings.auth.domain.GetUserUseCase
import com.example.todoapp.settings.auth.domain.SignInWithGoogleUseCase
import com.example.todoapp.settings.firestore.domain.SyncDataFromFirestoreUseCase
import com.example.todoapp.settings.firestore.domain.SyncDataWithFirebaseUseCase
import com.example.todoapp.settings.ui.UserUiState.Success
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val signInWithGoogleUseCase: SignInWithGoogleUseCase,
    private val addUserCaseUse: AddUserCaseUse,
    private val syncDataFromFirestoreUseCase: SyncDataFromFirestoreUseCase,
    private val syncDataWithFirestoreUseCase: SyncDataWithFirebaseUseCase,
    getUserUseCase: GetUserUseCase,
) : ViewModel() {

    val userUiState: StateFlow<UserUiState> =
        getUserUseCase().map{
            if (it != null) {
                Success(it)
            } else {
                UserUiState.Empty
            }
        }
            .catch { Throwable(it) }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), UserUiState.Loading)

    private fun signInWithGoogle(idToken: String) {
        viewModelScope.launch {
            signInWithGoogleUseCase(idToken, onSuccess = {
                val firebaseUser = FirebaseAuth.getInstance().currentUser
                firebaseUser?.let {
                    val userEntity =
                        UserEntity(
                            uid = it.uid,
                            name = it.displayName ?: "",
                            email = it.email ?: ""
                        )
                    viewModelScope.launch {
                        addUserCaseUse(userEntity)
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