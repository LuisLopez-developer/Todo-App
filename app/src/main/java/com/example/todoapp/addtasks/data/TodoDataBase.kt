package com.example.todoapp.addtasks.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.todoapp.addtasks.utils.Converters

@Database(entities = [TaskEntity::class], version = 1)
@TypeConverters(Converters::class)
abstract class TodoDataBase:RoomDatabase() {
    abstract fun taskDao(): TaskDao
}