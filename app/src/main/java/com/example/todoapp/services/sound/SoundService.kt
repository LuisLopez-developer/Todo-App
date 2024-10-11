package com.example.todoapp.services.sound

import android.content.Context
import android.media.Ringtone
import android.media.RingtoneManager

class SoundService(private val context: Context) {

    private var ringtone: Ringtone? = null

    fun playAlarmSound() {
        // Usar RingtoneManager para reproducir un sonido de alarma
        val alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
            ?: RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        // Obtener el Ringtone y reproducirlo
        if (ringtone == null) {
            ringtone = RingtoneManager.getRingtone(context, alarmUri)
            ringtone?.play()
        }
    }

    fun stopAlarmSound() {
        ringtone?.let {
            if (it.isPlaying) {
                it.stop()
                ringtone = null
            }
        }
    }

}