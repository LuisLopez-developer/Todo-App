package com.example.todoapp.taskcategory.data

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [CategoryEntity::class], version = 1)
abstract class CategoryDataBase : RoomDatabase() {
    abstract fun categoryDao(): CategoryDao

}