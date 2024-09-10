package com.example.todoapp.addtasks.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.todoapp.addtasks.domain.AddTaskUseCase
import com.example.todoapp.addtasks.domain.DeleteTaskUseCase
import com.example.todoapp.addtasks.domain.GetTaskByIdUseCase
import com.example.todoapp.addtasks.domain.GetTaskUseCase
import com.example.todoapp.addtasks.domain.UpdateTaskUseCase
import com.example.todoapp.addtasks.ui.TasksUiState.Error
import com.example.todoapp.addtasks.ui.TasksUiState.Loading
import com.example.todoapp.addtasks.ui.TasksUiState.Success
import com.example.todoapp.addtasks.ui.model.TaskModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.threeten.bp.LocalDate
import org.threeten.bp.LocalTime
import javax.inject.Inject

@HiltViewModel
class TaskViewModel @Inject constructor(
    private val addTaskUseCase: AddTaskUseCase,
    private val updateTaskUseCase: UpdateTaskUseCase,
    private val deleteTaskUseCase: DeleteTaskUseCase,
    getTaskUseCase: GetTaskUseCase,
    private val getTaskByIdUseCase: GetTaskByIdUseCase, // Añadido
) : ViewModel() {

    val uiState: StateFlow<TasksUiState> = getTaskUseCase().map(::Success)
        .catch { Error(it) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), Loading)

    private val _showDialog = MutableLiveData<Boolean>()
    val showDialog: LiveData<Boolean> = _showDialog

    private val _showDatePicker = MutableLiveData<Boolean>()
    val showDatePicker: LiveData<Boolean> = _showDatePicker

    private val _showTimePicker = MutableLiveData<Boolean>()
    val showTimePicker: LiveData<Boolean> = _showTimePicker

    fun onShowTimePicker() {
        _showTimePicker.value = true
    }

    fun onHideTimePicker() {
        _showTimePicker.value = false
    }

    fun onShowDatePicker() {
        _showDatePicker.value = true
    }

    fun onHideDatePicker() {
        _showDatePicker.value = false
    }

    private val _taskUiState = MutableLiveData<TaskUiState>(TaskUiState.Empty)
    val taskUiState: LiveData<TaskUiState> = _taskUiState

    fun onDialogClose() {
        _showDialog.value = false
    }

    fun onTaskCreated(
        task: String,
        startDate: LocalDate? = null,
        endDate: LocalDate? = null,
        time: LocalTime? = null,
        details: String? = null,
    ) {
        _showDialog.value = false
        viewModelScope.launch {
            addTaskUseCase(
                TaskModel(
                    task = task,
                    startDate = startDate,
                    endDate = endDate,
                    time = time,
                    details = details
                )
            )
        }
    }

    fun onShowDialogClick() {
        _showDialog.value = true
    }

    fun onCheckBox(taskModel: TaskModel) {
        viewModelScope.launch {
            updateTaskUseCase(taskModel.copy(selected = !taskModel.selected))
        }
    }

    fun updateTask(taskModel: TaskModel) {
        viewModelScope.launch {
            updateTaskUseCase(taskModel)
            // Notifica a la UI que la tarea ha sido actualizada
            _taskUiState.value = TaskUiState.Success(taskModel)
        }
    }

    fun onItemRemove(taskModel: TaskModel) {
        viewModelScope.launch {
            deleteTaskUseCase(taskModel)
        }
    }

    fun getTaskById(taskId: Int) {
        _taskUiState.value = TaskUiState.Loading
        viewModelScope.launch {
            try {
                val task = getTaskByIdUseCase.execute(taskId)
                _taskUiState.value = if (task != null) {
                    // Aquí actualizamos los valores temporales
                    setTemporaryDate(task.startDate)
                    setTemporaryTime(task.time)
                    TaskUiState.Success(task)
                } else {
                    TaskUiState.Empty
                }
            } catch (e: Exception) {
                _taskUiState.value = TaskUiState.Error(e)
            }
        }
    }


    private val _temporaryDate = MutableLiveData<LocalDate?>(null)
    val temporaryDate: LiveData<LocalDate?> = _temporaryDate

    private val _temporaryTime = MutableLiveData<LocalTime?>(null)
    val temporaryTime: LiveData<LocalTime?> = _temporaryTime

    fun onShowDateDialogClick() {
        // Verifica si el estado actual es Success antes de acceder a task
        val currentState = _taskUiState.value
        if (currentState is TaskUiState.Success) {
            _temporaryDate.value = currentState.task.startDate
            _temporaryTime.value = currentState.task.time
        }
        _showDatePicker.value = true
    }

    fun setTemporaryDate(date: LocalDate?) {
        _temporaryDate.value = date
    }

    fun setTemporaryTime(time: LocalTime?) {
        _temporaryTime.value = time
    }

    fun resetTemporaryDateTime() {
        _temporaryDate.value = null
        _temporaryTime.value = null
    }

    fun resetTaskDateTime(taskId: Int) {
        viewModelScope.launch {
            // Obtén la tarea actual
            val currentTask = getTaskByIdUseCase.execute(taskId)
            if (currentTask != null) {
                // Crea una nueva instancia de la tarea con startDate y time como null
                val updatedTask = currentTask.copy(startDate = null, time = null)
                // Actualiza la tarea en la base de datos
                updateTaskUseCase(updatedTask)
                // Actualiza el estado de la UI
                _taskUiState.value = TaskUiState.Success(updatedTask)
            }
        }
    }

}