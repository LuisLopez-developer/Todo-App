package com.example.todoapp.services.alarm

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.todoapp.constants.NotificationStructure
import com.example.todoapp.services.AlarmManager
import com.example.todoapp.utils.Logger
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class AlarmReceiver : BroadcastReceiver() {
    @Inject
    lateinit var alarmManager: AlarmManager

    override fun onReceive(context: Context, intent: Intent) {
        val id = intent.getIntExtra(NotificationStructure.ALARM_ID, -1)
        val title = intent.getStringExtra(NotificationStructure.ALARM_TITLE)
        Logger.debug("AlarmManager", "onReceive id: $id, title: $title")
        // Verificamos que el taskId sea válido, y que el título no sea nulo
        if (id != -1 && title != null) {
            alarmManager.onAlarmReceived(id, title)
        }
    }
}