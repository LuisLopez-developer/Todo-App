package com.example.todoapp.settings.auth.data.di

import com.example.todoapp.user.domain.model.UserItem
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class FirebaseModule {

    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth {
        return FirebaseAuth.getInstance()
    }

}

fun FirebaseUser.toDomain(): UserItem {
    return UserItem(
        uid = uid,
        name = displayName ?: "",
        email = email ?: ""
    )
}