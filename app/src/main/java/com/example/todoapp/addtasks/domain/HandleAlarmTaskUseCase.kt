package com.example.todoapp.addtasks.domain

import com.example.todoapp.addtasks.domain.model.TaskItem
import com.example.todoapp.services.AlarmManager
import org.threeten.bp.LocalDate
import org.threeten.bp.LocalTime
import javax.inject.Inject

class HandleAlarmTaskUseCase @Inject constructor(private val alarmManager: AlarmManager) {
    operator fun invoke(task: TaskItem, originalTask: TaskItem?) {
        // Cancelar alarma si existía en la tarea original
        originalTask?.time?.let {
            alarmManager.cancelAlarm(originalTask.id.hashCode(), originalTask.task)
        }

        // Si la tarea está seleccionada (realizada), no programar la alarma
        if (task.selected) return

        // Programar nueva alarma si tiene fecha futura
        task.time?.let { time ->
            val currentDate = LocalDate.now()
            val currentTime = LocalTime.now()

            if (task.startDate.isAfter(currentDate) ||
                (task.startDate.isEqual(currentDate) && time.isAfter(currentTime))
            ) {
                alarmManager.handleAlarmTrigger(
                    task.id.hashCode(),
                    task.task,
                    task.startDate,
                    task.time
                )
            }
        }
    }
}