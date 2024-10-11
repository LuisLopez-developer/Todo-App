package com.example.todoapp.addtasks.ui.editTask

import androidx.lifecycle.viewModelScope
import com.example.todoapp.addtasks.domain.DeleteTaskUseCase
import com.example.todoapp.addtasks.domain.GetTaskByIdFlowUseCase
import com.example.todoapp.addtasks.domain.UpdateTaskUseCase
import com.example.todoapp.addtasks.ui.BaseTaskViewModel
import com.example.todoapp.addtasks.ui.editTask.TaskUiState.Success
import com.example.todoapp.addtasks.ui.model.toViewModel
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
import javax.inject.Inject

@HiltViewModel
class TaskEditViewModel @Inject constructor(
    private val getTaskByIdFlowUseCase: GetTaskByIdFlowUseCase,
    updateTaskUseCase: UpdateTaskUseCase, deleteTaskUseCase: DeleteTaskUseCase,
) : BaseTaskViewModel(updateTaskUseCase, deleteTaskUseCase) {

    private val _taskId = MutableStateFlow<String?>(null)
    val taskId: StateFlow<String?> = _taskId

    fun setTaskId(taskId: String) {
        _taskId.value = taskId
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    val taskByIdState: StateFlow<TaskUiState> = taskId
        .flatMapLatest { taskId ->
            if (taskId != null) {
                getTaskByIdFlowUseCase(taskId).map { Success(it.toViewModel()) }
            } else {
                emptyFlow()
            }
        }.catch { TaskUiState.Error(it) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), TaskUiState.Loading)

}