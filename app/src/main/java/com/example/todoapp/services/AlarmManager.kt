package com.example.todoapp.services

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.example.todoapp.constants.NotificationStructure.ALARM_ID
import com.example.todoapp.constants.NotificationStructure.ALARM_TITLE
import com.example.todoapp.services.alarm.AlarmReceiver
import com.example.todoapp.services.alarm.AlarmService
import com.example.todoapp.services.alarm.StopAlarmReceiver
import com.example.todoapp.services.notification.NotificationService
import com.example.todoapp.services.permission.PermissionService
import com.example.todoapp.services.sound.SoundService
import com.example.todoapp.utils.Logger
import org.threeten.bp.LocalDate
import org.threeten.bp.LocalTime

class AlarmManager(
    private val alarmService: AlarmService,
    private val soundService: SoundService,
    private val notificationService: NotificationService,
    private val permissionService: PermissionService,
    private val context: Context,
) {

    fun handleAlarmTrigger(id: Int, title: String, date: LocalDate, time: LocalTime) {
        val pendingIntent = createPendingIntent(id, title)
        Logger.debug("AlarmManager", "Setting alarm for task: $id at ${date.atTime(time)}")
        alarmService.setAlarm(date, time, pendingIntent)
    }

    fun canScheduleExactAlarms(): Boolean {
        return alarmService.canScheduleExactAlarms()
    }

    fun onAlarmReceived(id: Int, title: String) {
        if(!permissionService.isNotificationPermissionGranted() || !canScheduleExactAlarms()){
            Logger.debug("AlarmManager", "Permisos insuficientes para mostrar notificación")
            return
        }

        soundService.playAlarmSound()
        val stopAlarmPendingIntent = createStopAlarmPendingIntent(id)
        notificationService.sendTaskNotification(id, title, stopAlarmPendingIntent)
    }

    fun stopAlarm(taskId: Int) {
        soundService.stopAlarmSound()
        notificationService.cancelNotification(taskId)
    }

    fun cancelAlarm(id: Int, title: String) {
        val pendingIntent = createPendingIntent(id, title)
        alarmService.cancelAlarm(pendingIntent)
    }

    private fun createPendingIntent(id: Int, title: String): PendingIntent {
        val intent = Intent(context, AlarmReceiver::class.java).apply {
            putExtra(ALARM_ID, id)
            putExtra(ALARM_TITLE, title)
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            id,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        return pendingIntent
    }

    private fun createStopAlarmPendingIntent(id: Int): PendingIntent {
        val stopAlarmIntent = Intent(context, StopAlarmReceiver::class.java).apply {
            putExtra(ALARM_ID, id)
        }

        return PendingIntent.getBroadcast(
            context,
            id,
            stopAlarmIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }
}