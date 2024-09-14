package com.example.todoapp.services.alarm

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.todoapp.R
import com.example.todoapp.constants.NotificationStructure.TASK_ID
import com.example.todoapp.constants.NotificationStructure.TASK_TITLE
import com.example.todoapp.services.notification.sendNotification

class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        val taskId = intent?.getIntExtra(TASK_ID, -1) ?: -1
        val title =
            intent?.getStringExtra(TASK_TITLE) ?: context.getString(R.string.task_notifications)

        sendNotification(context, taskId, title)
    }
}