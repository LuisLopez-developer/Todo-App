package com.example.todoapp.addtasks.ui.editTask

import android.content.Context
import android.util.Log
import androidx.lifecycle.viewModelScope
import com.example.todoapp.addtasks.domain.GetTaskByIdFlowUseCase
import com.example.todoapp.addtasks.domain.GetTaskByIdUseCase
import com.example.todoapp.addtasks.domain.UpdateTaskUseCase
import com.example.todoapp.addtasks.domain.model.toDomain
import com.example.todoapp.addtasks.ui.BaseTaskViewModel
import com.example.todoapp.addtasks.ui.editTask.TaskUiState.Success
import com.example.todoapp.addtasks.ui.model.TaskModel
import com.example.todoapp.addtasks.ui.model.toViewModel
import com.example.todoapp.services.alarm.cancelAlarm
import com.example.todoapp.services.alarm.setAlarm
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.threeten.bp.LocalDate
import org.threeten.bp.LocalTime
import javax.inject.Inject

@HiltViewModel
class TaskEditViewModel @Inject constructor(
    private val getTaskByIdFlowUseCase: GetTaskByIdFlowUseCase,
    private val getTaskByIdUseCase: GetTaskByIdUseCase,
    updateTaskUseCase: UpdateTaskUseCase
) : BaseTaskViewModel(updateTaskUseCase) {

    private val _taskId = MutableStateFlow<String?>(null)
    val taskId: StateFlow<String?> = _taskId

    fun setTaskId(taskId: String) {
        _taskId.value = taskId
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    val taskByIdState: StateFlow<TaskUiState> = taskId
        .flatMapLatest { taskId ->
            if (taskId != null) {
                getTaskByIdFlowUseCase(taskId).map{ Success(it.toViewModel()) }
            } else {
                emptyFlow()
            }
        }.catch { TaskUiState.Error(it) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), TaskUiState.Loading)


    fun onDateAndTime(updatedTask: TaskModel, context: Context) {
        viewModelScope.launch {
            try {
                // Guardar una copia de la tarea original antes de actualizarla
                val originalTask = getTaskByIdUseCase.execute(updatedTask.id)

                // Actualizar la tarea en el repositorio
                updateTaskUseCase(updatedTask.toDomain())

                // Si la tarea original tenía una alarma programada, cancelarla
                if (originalTask != null) {
                    if (originalTask.time != null) {
                        cancelAlarm(context, originalTask.id)
                    }
                }

                // Verificar si la nueva tarea tiene una hora y fecha en el futuro antes de programar la alarma
                if (updatedTask.time != null) {
                    val currentDate = LocalDate.now()
                    val currentTime = LocalTime.now()

                    if (updatedTask.startDate.isAfter(currentDate) ||
                        (updatedTask.startDate.isEqual(currentDate) && updatedTask.time.isAfter(
                            currentTime
                        ))
                    ) {
                        setAlarm(
                            context,
                            updatedTask.id,
                            updatedTask.startDate,
                            updatedTask.time,
                            updatedTask.task
                        )
                    }
                }
            } catch (e: Exception) {
                Log.e("error", e.message.toString())
            }
        }
    }
}