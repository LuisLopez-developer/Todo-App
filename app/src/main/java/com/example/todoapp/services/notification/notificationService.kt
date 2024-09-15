package com.example.todoapp.services.notification

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.todoapp.MainActivity
import com.example.todoapp.R
import com.example.todoapp.constants.NotificationStructure.TASK_ID
import com.example.todoapp.services.alarm.StopAlarmReceiver

fun sendNotification(context: Context, taskId: Int, title: String) {
    if (ActivityCompat.checkSelfPermission(
            context,
            Manifest.permission.POST_NOTIFICATIONS
        ) != PackageManager.PERMISSION_GRANTED
    ) {
        Log.d("NotificationService", "No se concedió el permiso para enviar notificaciones")
        return
    }

    val channelId = "task_notification_channel"
    val notificationManager = NotificationManagerCompat.from(context)

    // Crear el canal de notificación para Android 8.0 y superior
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val channel = NotificationChannel(
            channelId,
            "Task Notifications",
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = "Channel for task notifications"
        }
        notificationManager.createNotificationChannel(channel)
    }

    // Intent para abrir la aplicación
    val mainIntent = Intent(context, MainActivity::class.java).apply {
        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
    }
    val mainPendingIntent = PendingIntent.getActivity(
        context,
        0,
        mainIntent,
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )

    // Intent para detener el sonido de la alarma
    val stopAlarmIntent = Intent(context, StopAlarmReceiver::class.java).apply {
        putExtra(TASK_ID, taskId)
    }
    val stopAlarmPendingIntent = PendingIntent.getBroadcast(
        context,
        taskId,
        stopAlarmIntent,
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )

    // Crear la notificación con la acción de detener la alarma
    val notification = NotificationCompat.Builder(context, channelId)
        .setSmallIcon(R.drawable.ic_notifications)
        .setContentTitle(context.getString(R.string.scheduled_task))
        .setContentText(title)
        .setPriority(NotificationCompat.PRIORITY_HIGH)
        .setContentIntent(mainPendingIntent)
        .setAutoCancel(true)
        // Añadir el botón para detener la alarma
        .addAction(R.drawable.ic_alarm_off, "Detener Alarma", stopAlarmPendingIntent)
        .build()

    notificationManager.notify(taskId, notification)
}