package com.example.todoapp.services.alarm

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.Ringtone
import android.media.RingtoneManager
import android.net.Uri
import android.util.Log
import com.example.todoapp.R
import com.example.todoapp.constants.NotificationStructure.TASK_ID
import com.example.todoapp.constants.NotificationStructure.TASK_TITLE
import com.example.todoapp.services.notification.sendNotification

class AlarmReceiver : BroadcastReceiver() {

    companion object {
        var ringtone: Ringtone? = null

        // Método para detener el sonido
        fun stopAlarmSound() {
            ringtone?.let {
                if (it.isPlaying) {
                    it.stop()
                    ringtone = null
                }
            }
        }
    }

    override fun onReceive(context: Context, intent: Intent?) {
        val taskId = intent?.getIntExtra(TASK_ID, -1) ?: -1
        val title =
            intent?.getStringExtra(TASK_TITLE) ?: context.getString(R.string.task_notifications)

        // Enviar la notificación con la opción para detener la alarma
        sendNotification(context, taskId, title)

        // Reproducir el sonido de alarma
        playAlarmSound(context)
    }

    private fun playAlarmSound(context: Context) {
        Log.d("AlarmReceiver", "Reproduciendo sonido de alarma")
        // Usar RingtoneManager para reproducir un sonido de alarma
        val alarmUri: Uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
            ?: RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        // Obtener el Ringtone y reproducirlo
        if (ringtone == null) {
            ringtone = RingtoneManager.getRingtone(context, alarmUri)
            ringtone?.play()
        }
    }
}