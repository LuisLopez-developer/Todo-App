package com.example.todoapp.settings.auth.data

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import javax.inject.Inject


class AuthRepository @Inject constructor(private val auth: FirebaseAuth, private val userDao: UserDao) {

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