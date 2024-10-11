package com.example.todoapp.addtasks.domain

import android.util.Log
import com.example.todoapp.addtasks.data.TaskRepository
import com.example.todoapp.addtasks.data.toDatabase
import com.example.todoapp.addtasks.domain.model.TaskItem
import com.example.todoapp.services.AlarmManager
import com.example.todoapp.user.domain.GetUserUseCase
import kotlinx.coroutines.flow.first
import org.threeten.bp.LocalDate
import org.threeten.bp.LocalTime
import javax.inject.Inject

class AddTaskUseCase @Inject constructor(
    private val taskRepository: TaskRepository,
    private val getUserUseCase: GetUserUseCase,
    private val alarmManager: AlarmManager
) {
    suspend operator fun invoke(taskItem: TaskItem) {
        taskRepository.add(taskItem.toDatabase().copy(userId = getUserUseCase().first()?.uid))

        // Verificar si la fecha y hora están en el futuro antes de programar la alarma
        taskItem.time?.let { time ->
            val currentDate = LocalDate.now()
            val currentTime = LocalTime.now()

            if (taskItem.startDate.isAfter(currentDate) ||
                (taskItem.startDate.isEqual(currentDate) && time.isAfter(currentTime))
            ) {
                Log.d("AlarmManager", "Setting alarm for task: ${taskItem.id} at ${taskItem.startDate.atTime(time)}")
                alarmManager.handleAlarmTrigger(
                    taskItem.id.hashCode(),
                    taskItem.task,
                    taskItem.startDate,
                    taskItem.time
                )
            }
        }
    }
}