package com.example.todoapp.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.todoapp.addtasks.data.TaskDao
import com.example.todoapp.addtasks.data.TaskEntity
import com.example.todoapp.addtasks.utils.Converters
import com.example.todoapp.holidays.data.local.HolidayDao
import com.example.todoapp.holidays.data.local.HolidayEntity
import com.example.todoapp.state.data.StateDao
import com.example.todoapp.state.data.StateEntity
import com.example.todoapp.taskcategory.data.CategoryDao
import com.example.todoapp.taskcategory.data.CategoryEntity
import com.example.todoapp.user.data.UserDao
import com.example.todoapp.user.data.UserEntity

@Database(
    entities = [TaskEntity::class, CategoryEntity::class, UserEntity::class, HolidayEntity::class, StateEntity::class],
    version = 1,
    exportSchema = true
)
@TypeConverters(Converters::class)
abstract class TodoDataBase : RoomDatabase() {
    abstract fun taskDao(): TaskDao
    abstract fun categoryDao(): CategoryDao
    abstract fun userDao(): UserDao
    abstract fun stateDao(): StateDao
    abstract fun holidayDao(): HolidayDao

    companion object {
        @Volatile
        private var INSTANCE: TodoDataBase? = null

        fun getInstance(context: Context): TodoDataBase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    TodoDataBase::class.java,
                    "TaskDatabase"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}