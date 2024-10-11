package com.example.todoapp.settings.auth.domain

import com.example.todoapp.addtasks.domain.ReassignTasksToUserUseCase
import com.example.todoapp.settings.auth.data.di.toDomain
import com.example.todoapp.taskcategory.domain.ReassignCategoriesToUserUseCase
import com.example.todoapp.user.domain.AddUserCaseUse
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class HandleSignInUseCase @Inject constructor(
    private val auth: FirebaseAuth,
    private val addUserCaseUse: AddUserCaseUse,
    private val reassignCategoriesToUserUseCase: ReassignCategoriesToUserUseCase,
    private val reassignTasksToUserUseCase: ReassignTasksToUserUseCase,
) {
    suspend operator fun invoke(credentialResponse: AuthCredential) {

        val user = auth.signInWithCredential(credentialResponse).await().user ?: return

        // Reasignar datos de usuario y tareas
        processUserSignIn(user)
    }

    private suspend fun processUserSignIn(user: FirebaseUser) {
        addUserCaseUse(user.toDomain())
        reassignTasksToUserUseCase(user.uid)
        reassignCategoriesToUserUseCase(user.uid)
    }

}