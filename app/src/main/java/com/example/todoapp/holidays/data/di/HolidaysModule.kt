package com.example.todoapp.holidays.data.di

import com.example.todoapp.data.TodoDataBase
import com.example.todoapp.holidays.data.local.HolidayDao
import com.example.todoapp.holidays.data.network.HoliDaysService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
class HolidaysModule {

    @Provides
    fun provideHolidaysService(): HoliDaysService {
        return HoliDaysService()
    }

    @Provides
    fun provideHolidayDao(todoDataBase: TodoDataBase): HolidayDao {
        return todoDataBase.holidayDao()
    }
}