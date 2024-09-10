package com.example.todoapp.addtasks.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.todoapp.addtasks.domain.AddTaskUseCase
import com.example.todoapp.addtasks.domain.DeleteTaskUseCase
import com.example.todoapp.addtasks.domain.GetTaskUseCase
import com.example.todoapp.addtasks.domain.UpdateTaskUseCase
import com.example.todoapp.addtasks.domain.GetTaskByIdUseCase // Añadido
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
    private val getTaskByIdUseCase: GetTaskByIdUseCase // Añadido
) : ViewModel() {

    val uiState: StateFlow<TasksUiState> = getTaskUseCase().map(::Success)
        .catch { Error(it) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), Loading)

    private val _showDialog = MutableLiveData<Boolean>()
    val showDialog: LiveData<Boolean> = _showDialog

    private val _showTimePicker = MutableLiveData<Boolean>()
    val showTimePicker: LiveData<Boolean> = _showTimePicker

    fun onShowTimePicker() {
        _showTimePicker.value = true
    }

    fun onHideTimePicker() {
        _showTimePicker.value = false
    }

    private val _taskUiState = MutableLiveData<TaskUiState>(TaskUiState.Empty)
    val taskUiState: LiveData<TaskUiState> = _taskUiState

    fun onDialogClose() {
        _showDialog.value = false
    }

    fun onTaskCreated(task: String, startDate: LocalDate? = null, endDate: LocalDate? = null, time: LocalTime? = null, details: String? = null) {
        _showDialog.value = false
        viewModelScope.launch {
            addTaskUseCase(TaskModel(task = task, startDate = startDate, endDate = endDate, time = time, details = details))
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
                    TaskUiState.Success(task)
                } else {
                    TaskUiState.Empty
                }
            } catch (e: Exception) {
                _taskUiState.value = TaskUiState.Error(e)
            }
        }
    }
}

