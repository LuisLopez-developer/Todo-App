package com.example.todoapp.settings.di

import com.example.todoapp.addtasks.data.TaskDao
import com.example.todoapp.settings.firestore.data.FirebaseRepository
import com.example.todoapp.taskcategory.data.CategoryDao
import com.google.firebase.auth.FirebaseAuth
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
    fun provideFirebaseRepository(categoryDao: CategoryDao, taskDao: TaskDao): FirebaseRepository {
        return FirebaseRepository(categoryDao, taskDao)
    }

    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth {
        return FirebaseAuth.getInstance()
    }

}