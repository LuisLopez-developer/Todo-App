package com.example.todoapp.services.alarm

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationManagerCompat
import com.example.todoapp.constants.NotificationStructure.TASK_ID

class StopAlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        Log.d("StopAlarmReceiver", "Deteniendo sonido de alarma")

        // Detener el sonido de la alarma
        AlarmReceiver.stopAlarmSound()

        // Eliminar la notificación
        val taskId = intent?.getIntExtra(TASK_ID, -1) ?: -1
        if (taskId != -1) {
            Log.d("StopAlarmReceiver", "Eliminando la notificación con ID: $taskId")
            val notificationManager = NotificationManagerCompat.from(context)
            notificationManager.cancel(taskId) // Cancela la notificación con el ID específico
        }
    }
}