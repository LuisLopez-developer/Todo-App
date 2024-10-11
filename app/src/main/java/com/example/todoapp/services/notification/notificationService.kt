package com.example.todoapp.services.notification

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.todoapp.MainActivity
import com.example.todoapp.R

class NotificationService(private val context: Context) {

    private val notificationManager = NotificationManagerCompat.from(context)

    @SuppressLint("MissingPermission")
    fun sendTaskNotification(taskId: Int, title: String, stopAlarmPendingIntent: PendingIntent) {
        val chanelId = createNotificationChannel()

        val mainIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val mainPendingIntent = PendingIntent.getActivity(
            context,
            0,
            mainIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, chanelId)
            .setSmallIcon(R.drawable.ic_notifications)
            .setContentTitle(context.getString(R.string.scheduled_task))
            .setContentText(title)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(mainPendingIntent)
            .addAction(
                R.drawable.ic_alarm_off,
                context.getString(R.string.stop_alarm),
                stopAlarmPendingIntent
            )
            .setAutoCancel(true)
            .build()


        notificationManager.notify(taskId, notification)

    }

    private fun createNotificationChannel(): String {
        val channelId = "task_notification_channel"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId, "Task Notifications", NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Channel for task notifications"
            }
            notificationManager.createNotificationChannel(channel)
        }
        return channelId
    }

    fun cancelNotification(taskId: Int) {
        notificationManager.cancel(taskId)
    }
}