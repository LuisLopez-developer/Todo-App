package com.example.todoapp.data.di

import android.content.Context
import android.util.Log
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.todoapp.addtasks.data.TaskDao
import com.example.todoapp.data.TodoDataBase
import com.example.todoapp.state.data.StateDao
import com.example.todoapp.state.data.seedStates
import com.example.todoapp.taskcategory.data.CategoryDao
import com.example.todoapp.taskcategory.data.CategoryEntity
import com.example.todoapp.user.data.UserDao
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
    fun provideStateDao(todoDataBase: TodoDataBase): StateDao {
        return todoDataBase.stateDao()
    }

    @Provides
    @Singleton
    fun provideTodoDatabase(@ApplicationContext appContext: Context): TodoDataBase {
        return Room.databaseBuilder(appContext, TodoDataBase::class.java, "TaskDatabase")
            .addCallback(object : RoomDatabase.Callback() {
                override fun onCreate(db: SupportSQLiteDatabase) {
                    super.onCreate(db)
                    // Aquí lanzamos una corrutina para insertar los seeders en la base de datos
                    CoroutineScope(Dispatchers.IO).launch {
                        try {
                            val stateDao = provideStateDao(TodoDataBase.getInstance(appContext))
                            seedStates().forEach {
                                stateDao.addState(it)
                            }
                            Log.d("DatabaseModule", "Estados predeterminados insertados correctamente")
                        } catch (e: Exception) {
                            Log.e("DatabaseModule", "Error al insertar estados predeterminados", e)
                        }

                        try {
                            val prepopulateCategories = listOf(
                                CategoryEntity(id = "1d4e5f6a-7b8c-9d0e-1f2a-3b4c5d6e7f8a", category = "Trabajo"),
                                CategoryEntity(id = "2d4e5f6a-7b8c-9d0e-1f2a-3b4c5d6e7f8b", category = "Estudio"),
                                CategoryEntity(id = "3d4e5f6a-7b8c-9d0e-1f2a-3b4c5d6e7f8c", category = "Familia"),
                                CategoryEntity(id = "4d4e5f6a-7b8c-9d0e-1f2a-3b4c5d6e7f8d", category = "Compras"),
                                CategoryEntity(id = "5d4e5f6a-7b8c-9d0e-1f2a-3b4c5d6e7f8e", category = "Examen")
                            )
                            val categoryDao =
                                provideCategoryDao(TodoDataBase.getInstance(appContext))
                            prepopulateCategories.forEach {
                                categoryDao.addCategory(it)
                            }
                            Log.d(
                                "DatabaseModule",
                                "Categorías predeterminadas insertadas correctamente"
                            )

                        } catch (e: Exception) {
                            Log.e(
                                "DatabaseModule",
                                "Error al insertar categorías predeterminadas",
                                e
                            )
                        }
                    }
                }
            })
            .build()
    }
}