package com.example.todoapp.addtasks.ui

import android.content.Context
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
import com.example.todoapp.services.alarm.cancelAlarm
import com.example.todoapp.services.alarm.setAlarm
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

    fun onHideDatePicker() {
        _showDatePicker.value = false
    }

    fun onDialogClose() {
        _showDialog.value = false
    }

    fun onTaskCreated(task: TaskModel, context: Context) {
        _showDialog.value = false

        viewModelScope.launch {
            addTaskUseCase(task)

            // Verificar si la fecha y hora están en el futuro antes de programar la alarma
            task.time?.let { time ->
                val currentDate = LocalDate.now()
                val currentTime = LocalTime.now()

                if (task.startDate.isAfter(currentDate) ||
                    (task.startDate.isEqual(currentDate) && time.isAfter(currentTime))
                ) {
                    setAlarm(context, task.id, task.startDate, time, task.task)
                }
            }
        }
    }

    fun onShowDialogClick() {
        _showDialog.value = true
    }

    fun onTask(taskModel: TaskModel){
        viewModelScope.launch {
            updateTaskUseCase(taskModel.copy(task = taskModel.task))
        }
    }

    fun onDetails(taskModel: TaskModel){
        viewModelScope.launch {
            updateTaskUseCase(taskModel.copy(details = taskModel.details))
        }
    }

    fun onCategory(taskModel: TaskModel){
        viewModelScope.launch {
            updateTaskUseCase(taskModel.copy(categoryId = taskModel.categoryId))
        }
    }

    fun onCheckBox(taskModel: TaskModel, context: Context) {
        viewModelScope.launch {
            try {
                // Obtener la tarea original antes de actualizarla
                val originalTask = getTaskByIdUseCase.execute(taskModel.id)

                if (originalTask != null) {
                    // Si la tarea original tiene una alarma programada
                    if (originalTask.time != null) {
                        // Verificar si la tarea original esta seleccionada (Realizada)
                        // debido a que si esta seleccionada significa que al llamar a esta función
                        // se esta deseleccionando la tarea y viceversa
                        if (originalTask.selected) { // Si la tarea original esta seleccionada (Realizada)
                            setAlarm(
                                context,
                                originalTask.id,
                                originalTask.startDate,
                                originalTask.time,
                                originalTask.task
                            )
                        }else{ // Si la tarea original no esta seleccionada (No realizada)
                            cancelAlarm(context, originalTask.id)
                        }

                    }
                }

                updateTaskUseCase(taskModel.copy(selected = taskModel.selected))

            } catch (e: Exception) {
                Log.e("error", e.message.toString())
            }
        }
    }

    fun updateTask(updatedTask: TaskModel, context: Context) {
        viewModelScope.launch {
            try {
                // Guardar una copia de la tarea original antes de actualizarla
                val originalTask = getTaskByIdUseCase.execute(updatedTask.id)

                // Actualizar la tarea en el repositorio
                updateTaskUseCase(updatedTask)

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

                // Actualizar el estado de la UI con la tarea actualizada
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
        viewModelScope.launch {
            try {
                val task = getTaskByIdUseCase.execute(taskId)

                if (task != null) {
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