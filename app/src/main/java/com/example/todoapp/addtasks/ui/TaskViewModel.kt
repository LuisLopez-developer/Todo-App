package com.example.todoapp.addtasks.ui

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asFlow
import androidx.lifecycle.viewModelScope
import com.example.todoapp.addtasks.domain.AddTaskUseCase
import com.example.todoapp.addtasks.domain.DeleteTaskUseCase
import com.example.todoapp.addtasks.domain.GetTaskByIdUseCase
import com.example.todoapp.addtasks.domain.GetTaskUseCase
import com.example.todoapp.addtasks.domain.GetTasksByDateUseCase
import com.example.todoapp.addtasks.domain.UpdateTaskUseCase
import com.example.todoapp.addtasks.ui.TasksUiState.Error
import com.example.todoapp.addtasks.ui.TasksUiState.Loading
import com.example.todoapp.addtasks.ui.TasksUiState.Success
import com.example.todoapp.addtasks.ui.model.TaskModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flatMapLatest
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
    private val getTaskUseCase: GetTaskUseCase,
    private val getTaskByIdUseCase: GetTaskByIdUseCase,
    private val getTasksByDateUseCase: GetTasksByDateUseCase,
) : ViewModel() {
    // Flujo que mantiene todas las fechas de tareas
    private val _taskDatesFlow = MutableStateFlow<List<LocalDate>>(emptyList())
    val taskDatesFlow: StateFlow<List<LocalDate>> = _taskDatesFlow

    init {
        // Cargar todas las fechas al inicializar el ViewModel
        getAllTaskDates()
    }

    private fun getAllTaskDates() {
        viewModelScope.launch {
            getTaskUseCase().map { taskList ->
                // Extraemos solo las fechas de las tareas
                taskList.map { it.startDate }
            }.collect { dates ->
                _taskDatesFlow.value = dates
            }
        }
    }

    // Estado para recuperar todas las tareas
    // Por ahora no se usa
    // NOTA: No Eliminar
    val uiState: StateFlow<TasksUiState> = getTaskUseCase().map(::Success)
        .catch { Error(it) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), Loading)

    // Sirve para dar un valor incial a "tasksByDateState"
    private val _selectedDate = MutableLiveData(LocalDate.now())
    var selectedDate: LiveData<LocalDate> = _selectedDate

    // Método para actualizar la fecha seleccionada
    fun setDate(date: LocalDate) {
        _selectedDate.value = date
    }


    // Estado para recuperar todas las tareas de un día en especifico
    @OptIn(ExperimentalCoroutinesApi::class)
    val tasksByDateState: StateFlow<TasksUiState> = _selectedDate.asFlow()
        .flatMapLatest { date ->
            getTasksByDateUseCase(date).map(::Success)
        }
        .catch { Error(it) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), Loading)


    private val _taskFlowUiState = MutableStateFlow<TaskUiState>(TaskUiState.Empty)
    val taskFlowUiState: StateFlow<TaskUiState> = _taskFlowUiState

    private val _showDialog = MutableStateFlow(false)
    val showDialog: StateFlow<Boolean> = _showDialog

    private val _showDatePicker = MutableStateFlow(false)
    val showDatePicker: StateFlow<Boolean> = _showDatePicker

    private val _showTimePicker = MutableStateFlow(false)
    val showTimePicker: StateFlow<Boolean> = _showTimePicker

    fun onHideDatePicker() {
        _showDatePicker.value = false
    }

    fun onDialogClose() {
        _showDialog.value = false
    }

    fun onTaskCreated(
        task: String,
        startDate: LocalDate = LocalDate.now(),
        endDate: LocalDate? = null,
        time: LocalTime? = null,
        details: String? = null,
        category: String? = null,
    ) {
        _showDialog.value = false

        viewModelScope.launch {
            addTaskUseCase(
                TaskModel(
                    task = task,
                    startDate = startDate,
                    endDate = endDate,
                    time = time,
                    details = details,
                    category = category
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

    fun updateTask(updatedTask: TaskModel) {
        // Implementa la lógica para actualizar la tarea en la base de datos o repositorio
        viewModelScope.launch {
            try {
                updateTaskUseCase(updatedTask) // Suponiendo que uses un repositorio para manejar las tareas
                _taskFlowUiState.value = TaskUiState.Success(updatedTask)
            } catch (e: Exception) {
                _taskFlowUiState.value =
                    TaskUiState.Error(throwable = Throwable("Error al actualizar la tarea"))
                Log.e("error", e.message.toString())
            }
        }
    }


    fun onItemRemove(taskModel: TaskModel) {
        viewModelScope.launch {
            deleteTaskUseCase(taskModel)
        }
    }

    fun getTaskById(taskId: Int) {
        _taskFlowUiState.value = TaskUiState.Loading
        viewModelScope.launch {
            try {
                val task = getTaskByIdUseCase.execute(taskId)
                if (task != null) {
                    // Actualiza las fechas temporales con la fecha y hora de la tarea
                    _temporaryDate.value = task.startDate
                    _temporaryTime.value = task.time

                    _taskFlowUiState.value = TaskUiState.Success(task)
                } else {
                    _taskFlowUiState.value = TaskUiState.Empty
                }
            } catch (e: Exception) {
                _taskFlowUiState.value = TaskUiState.Error(e)
            }
        }
    }

    private val _temporaryDate = MutableStateFlow<LocalDate?>(null)
    val temporaryDate: StateFlow<LocalDate?> = _temporaryDate

    private val _temporaryTime = MutableStateFlow<LocalTime?>(null)
    val temporaryTime: StateFlow<LocalTime?> = _temporaryTime

    fun onShowDateDialogClick() {
        // Verifica si el estado actual es Success antes de acceder a task
        val currentState = _taskFlowUiState.value
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

}