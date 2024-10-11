package com.example.todoapp.settings.auth.domain

import com.example.todoapp.addtasks.domain.ReassignTasksToUserUseCase
import com.example.todoapp.taskcategory.domain.ReassignCategoriesToUserUseCase
import com.example.todoapp.user.domain.DeleteAllUsersUseCase
import com.google.firebase.auth.FirebaseAuth
import javax.inject.Inject

class SignOutUseCase @Inject constructor(
    private val auth: FirebaseAuth,
    private val reassignCategoriesToUserUseCase: ReassignCategoriesToUserUseCase,
    private val reassignTasksToUserUseCase: ReassignTasksToUserUseCase,
    private val deleteAllUsersUseCase: DeleteAllUsersUseCase,
) {
    suspend operator fun invoke() {
        auth.signOut()

        processUserSignOut()
    }

    private suspend fun processUserSignOut() {
        reassignTasksToUserUseCase(null)
        reassignCategoriesToUserUseCase(null)
        deleteAllUsersUseCase()
    }
}