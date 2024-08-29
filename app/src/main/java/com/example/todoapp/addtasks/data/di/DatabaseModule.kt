package com.example.todoapp.addtasks.data.di

import android.content.Context
import androidx.room.Room
import com.example.todoapp.addtasks.data.TodoDataBase
import com.example.todoapp.addtasks.data.TaskDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class DatabaseModule {

    @Provides
    fun provideTaskDao(todoDataBase: TodoDataBase): TaskDao {
        return todoDataBase.taskDao()
    }

    @Provides
    @Singleton
    fun provideTodoDatabase(@ApplicationContext appContext: Context): TodoDataBase{
        return Room.databaseBuilder(appContext, TodoDataBase::class.java, "TaskDatabase").build()
    }
}