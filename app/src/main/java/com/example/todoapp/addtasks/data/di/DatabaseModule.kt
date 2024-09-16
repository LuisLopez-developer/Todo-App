package com.example.todoapp.addtasks.data.di

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.todoapp.addtasks.data.TodoDataBase
import com.example.todoapp.addtasks.data.TaskDao
import com.example.todoapp.settings.data.UserDao
import com.example.todoapp.taskcategory.data.CategoryDao
import com.example.todoapp.taskcategory.data.CategoryEntity
import com.google.firebase.auth.FirebaseAuth
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class DatabaseModule {

    @Provides
    fun provideTaskDao(todoDataBase: TodoDataBase): TaskDao {
        return todoDataBase.taskDao()
    }

    @Provides
    fun provideCategoryDao(todoDataBase: TodoDataBase): CategoryDao {
        return todoDataBase.categoryDao()
    }

    @Provides
    fun provideUserDao(todoDataBase: TodoDataBase): UserDao {
        return todoDataBase.userDao()
    }

    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth {
        return FirebaseAuth.getInstance()
    }

    @Provides
    @Singleton
    fun provideTodoDatabase(@ApplicationContext appContext: Context): TodoDataBase {
        return Room.databaseBuilder(appContext, TodoDataBase::class.java, "TaskDatabase")
            .addCallback(object : RoomDatabase.Callback() {
                override fun onCreate(db: SupportSQLiteDatabase) {
                    super.onCreate(db)
                    // Aquí lanzamos una corrutina para insertar las categorías iniciales
                    CoroutineScope(Dispatchers.IO).launch {
                        val prepopulateCategories = listOf(
                            CategoryEntity(category = "Trabajo"),
                            CategoryEntity(category = "Estudio"),
                            CategoryEntity(category = "Familia"),
                            CategoryEntity(category = "Compras"),
                            CategoryEntity(category = "Examen")
                        )
                        val categoryDao = provideCategoryDao(TodoDataBase.getInstance(appContext))
                        prepopulateCategories.map {
                            categoryDao.addCategory(it)
                        }
                    }
                }
            })
            .build()
    }
}