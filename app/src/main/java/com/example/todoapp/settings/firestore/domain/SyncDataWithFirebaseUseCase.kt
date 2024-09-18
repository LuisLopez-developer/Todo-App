package com.example.todoapp.settings.firestore.domain

import com.example.todoapp.settings.firestore.data.FirebaseRepository
import javax.inject.Inject

class SyncDataWithFirebaseUseCase @Inject constructor(private val firebaseRepository: FirebaseRepository) {
    suspend operator fun invoke() {
        firebaseRepository.syncDataWithFirebase()
    }
}