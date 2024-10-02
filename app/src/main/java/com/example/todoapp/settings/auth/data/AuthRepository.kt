package com.example.todoapp.settings.auth.data

import com.example.todoapp.settings.auth.ui.model.UserModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
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

    fun signInWithGoogle(idToken: String, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    onSuccess()
                } else {
                    onFailure(task.exception ?: Exception("Unknown error"))
                }
            }
    }
}

fun UserEntity.toUserModel() = UserModel(uid, name, email)