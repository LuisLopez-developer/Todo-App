package com.example.todoapp.services.alarm

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.todoapp.constants.NotificationStructure
import com.example.todoapp.services.AlarmManager
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class StopAlarmReceiver : BroadcastReceiver() {

    @Inject
    lateinit var alarmManager: AlarmManager

    override fun onReceive(context: Context, intent: Intent) {
        val taskId = intent.getIntExtra(NotificationStructure.ALARM_ID, -1)

        // Verificamos que el taskId sea válido
        if (taskId != -1) {
            alarmManager.stopAlarm(taskId)
        }
    }
}