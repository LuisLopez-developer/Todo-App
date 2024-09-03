package com.example.todoapp.taskcategory.data.di

import android.content.Context
import androidx.room.Room
import com.example.todoapp.addtasks.data.TaskDao
import com.example.todoapp.addtasks.data.TodoDataBase
import com.example.todoapp.taskcategory.data.CategoryDao
import com.example.todoapp.taskcategory.data.CategoryDataBase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
class CategoryDatabaseModule {

    @Provides
    fun provideCategoryDao(categoryDataBase: CategoryDataBase): CategoryDao {
        return categoryDataBase.categoryDao()
    }

    @Provides
    @Singleton
    fun provideCategoryDatabase(@ApplicationContext appContext: Context): CategoryDataBase{
        return Room.databaseBuilder(appContext, CategoryDataBase::class.java, "CategoryDatabase").build()
    }

}