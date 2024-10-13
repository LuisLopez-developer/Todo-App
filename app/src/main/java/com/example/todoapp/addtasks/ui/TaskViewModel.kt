package com.example.todoapp.addtasks.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asFlow
import androidx.lifecycle.viewModelScope
import com.example.todoapp.addtasks.domain.AddTaskUseCase
import com.example.todoapp.addtasks.domain.DeleteTaskUseCase
import com.example.todoapp.addtasks.domain.GetTaskUseCase
import com.example.todoapp.addtasks.domain.GetTasksByDateUseCase
import com.example.todoapp.addtasks.domain.RecreateAlarmsForTasksUseCase
import com.example.todoapp.addtasks.domain.UpdateTaskUseCase
import com.example.todoapp.addtasks.domain.model.toDomain
import com.example.todoapp.addtasks.ui.TasksUiState.Error
import com.example.todoapp.addtasks.ui.TasksUiState.Loading
import com.example.todoapp.addtasks.ui.TasksUiState.Success
import com.example.todoapp.addtasks.ui.model.TaskModel
import com.example.todoapp.addtasks.ui.model.toViewModelList
import com.example.todoapp.alarm.domain.AreBasicPermissionsGrantedUseCase
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
import javax.inject.Inject

@HiltViewModel
class TaskViewModel @Inject constructor(
    private val addTaskUseCase: AddTaskUseCase,
    private val getTaskUseCase: GetTaskUseCase,
    private val getTasksByDateUseCase: GetTasksByDateUseCase,
    private val areBasicPermissionsGrantedUseCase: AreBasicPermissionsGrantedUseCase,
    private val recreateAlarmsForTasksUseCase: RecreateAlarmsForTasksUseCase,
    updateTaskUseCase: UpdateTaskUseCase, deleteTaskUseCase: DeleteTaskUseCase,
) : BaseTaskViewModel(updateTaskUseCase, deleteTaskUseCase) {

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
            getTasksByDateUseCase(date).map { Success(it.toViewModelList()) }
        }
        .catch { Error(it) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), Loading)


    private val _showDialog = MutableStateFlow(false)
    val showDialog: StateFlow<Boolean> = _showDialog

    fun onDialogClose() {
        _showDialog.value = false
    }

    fun onTaskCreated(task: TaskModel) {
        _showDialog.value = false

        viewModelScope.launch {
            addTaskUseCase(task.toDomain())
        }
        checkTaskForTimeAndPermissions(task)
    }

    fun onShowDialogClick() {
        _showDialog.value = true
    }

    private val _showPermissionDialog = MutableStateFlow(false)
    val showPermissionDialog: StateFlow<Boolean> = _showPermissionDialog

    fun onShowPermissionDialog() {
        _showPermissionDialog.value = !_showPermissionDialog.value
    }

    fun recreateAlarms() {
        viewModelScope.launch {
            recreateAlarmsForTasksUseCase()
        }
    }

    fun checkTaskForTimeAndPermissions(task: TaskModel) {
        if (task.time != null) {
            // Si la tarea tiene una hora, comprobamos los permisos
            if (!areBasicPermissionsGrantedUseCase()) {
                // Si no tiene los permisos, mostramos el diálogo
                onShowPermissionDialog()
            }
        }
    }
}