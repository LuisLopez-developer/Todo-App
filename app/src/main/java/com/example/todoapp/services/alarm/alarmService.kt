package com.example.todoapp.services.alarm

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.example.todoapp.utils.Logger
import org.threeten.bp.LocalDate
import org.threeten.bp.LocalTime
import java.util.Calendar

class AlarmService(private val context: Context) {
    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    fun setAlarm(date: LocalDate, time: LocalTime, pendingIntent: PendingIntent) {
        val calendar = Calendar.getInstance().apply {
            set(date.year, date.monthValue - 1, date.dayOfMonth, time.hour, time.minute)
        }

        val triggerTime = calendar.timeInMillis

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
            if (alarmManager.canScheduleExactAlarms()) {
                Logger.debug("AlarmManager", "Programando alarma exacta")
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent)
            } else {
                Logger.debug("AlarmManager", "Solicicitar permiso para programar alarma exacta")
                requestExactAlarmPermission()
            }
        } else {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent)
        }
    }

    fun cancelAlarm(pendingIntent: PendingIntent) {
        Logger.debug("AlarmManager", "Canceling alarm")
        alarmManager.cancel(pendingIntent)
    }

    fun canScheduleExactAlarms(): Boolean {
        return if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
            Logger.debug("AlarmManager", "Comprobar si se pueden programar alarmas exactas")
            // Solo a partir de Android 12 (S) se puede usar canScheduleExactAlarms()
            alarmManager.canScheduleExactAlarms()
        } else {
            Logger.debug("AlarmManager", "Se asume que se pueden programar alarmas exactas")
            // Se asume que en versiones anteriores se puede programar alarmas exactas
            true
        }
    }

    private fun requestExactAlarmPermission() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
            val request =
                Intent(android.provider.Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM).apply {
                    data = android.net.Uri.parse("package:${context.packageName}")
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
            context.startActivity(request)
        }
    }

}