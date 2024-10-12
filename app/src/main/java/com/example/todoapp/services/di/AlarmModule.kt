package com.example.todoapp.services.di

import android.content.Context
import com.example.todoapp.services.AlarmManager
import com.example.todoapp.services.alarm.AlarmService
import com.example.todoapp.services.notification.NotificationService
import com.example.todoapp.services.sound.SoundService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AlarmModule {

    @Provides
    @Singleton
    fun provideAlarmService(@ApplicationContext context: Context): AlarmService {
        return AlarmService(context)
    }

    @Provides
    @Singleton
    fun provideSoundService(@ApplicationContext context: Context): SoundService {
        return SoundService(context)
    }

    @Provides
    @Singleton
    fun provideNotificationService(@ApplicationContext context: Context): NotificationService {
        return NotificationService(context)
    }

    @Provides
    @Singleton
    fun provideAlarmManager(
        alarmService: AlarmService,
        soundService: SoundService,
        notificationService: NotificationService,
        @ApplicationContext context: Context,
    ): AlarmManager {
        return AlarmManager(alarmService, soundService, notificationService, context)
    }

}