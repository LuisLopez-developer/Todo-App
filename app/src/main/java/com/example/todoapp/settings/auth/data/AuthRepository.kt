package com.example.todoapp.settings.auth.data

import android.util.Log
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialResponse
import com.example.todoapp.settings.auth.ui.model.UserModel
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject


class AuthRepository @Inject constructor(
    private val auth: FirebaseAuth,
    private val userDao: UserDao,
) {
    val user: Flow<UserModel?> = userDao.getUser().map { it?.toUserModel() }

    suspend fun deleteAllUsers() {
        userDao.deleteAllUsers()
    }

    suspend fun add(user: UserEntity) {
        userDao.addUser(user)
    }

    fun signInWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val firebaseUser = auth.currentUser
                    firebaseUser?.let {
                        val userEntity = UserEntity(
                            uid = it.uid,
                            name = it.displayName ?: "",
                            email = it.email ?: ""
                        )
                        CoroutineScope(Dispatchers.IO).launch {
                            add(userEntity)
                        }
                    }
                } else {
                    Log.e("TAG", "signInWithCredential:failure", task.exception)
                }
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

    fun signOut() {
        auth.signOut()
        CoroutineScope(Dispatchers.IO).launch {
            deleteAllUsers()
        }
    }

}

fun UserEntity.toUserModel() = UserModel(uid, name, email)