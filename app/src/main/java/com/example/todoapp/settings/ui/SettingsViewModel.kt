package com.example.todoapp.settings.ui

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.todoapp.addtasks.data.TaskRepository
import com.example.todoapp.settings.auth.data.UserEntity
import com.example.todoapp.settings.auth.domain.AddUserCaseUse
import com.example.todoapp.settings.auth.domain.SignInWithGoogleUseCase
import com.example.todoapp.settings.firestore.data.FirebaseRepository
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val signInWithGoogleUseCase: SignInWithGoogleUseCase,
    private val addUserCaseUse: AddUserCaseUse,
    private val taskRepository: TaskRepository,
    private val firebaseRepository: FirebaseRepository,
) : ViewModel() {
    var user by mutableStateOf<UserEntity?>(null)
        private set

    fun signInWithGoogle(idToken: String) {
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
                        user = userEntity
                    }
                }
            }, onFailure = {
                Log.e("SettingsViewModel", "signInWithGoogle: ${it.message}")
            })
        }
    }

    fun checkUser() {
        val firebaseUser = FirebaseAuth.getInstance().currentUser
        firebaseUser?.let {
            user = UserEntity(uid = it.uid, name = it.displayName ?: "", email = it.email ?: "")
        }
    }

    fun syncTasks() {
        viewModelScope.launch {
            taskRepository.syncTasksWithFirebase()
        }
    }

    fun syncTasksFromFirebase() {
        viewModelScope.launch {
            firebaseRepository.syncTasksFromFirestore()
        }
    }
}