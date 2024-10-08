package com.example.todoapp.addtasks.ui.taskList

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.todoapp.addtasks.domain.GetTaskByCategoryUseCase
import com.example.todoapp.addtasks.domain.GetTaskUseCase
import com.example.todoapp.addtasks.ui.TasksUiState
import com.example.todoapp.addtasks.ui.TasksUiState.Error
import com.example.todoapp.addtasks.ui.TasksUiState.Loading
import com.example.todoapp.addtasks.ui.TasksUiState.Success
import com.example.todoapp.addtasks.ui.model.toViewModelList
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class TaskListViewModel @Inject constructor(
    private val getTaskByCategoryUseCase: GetTaskByCategoryUseCase,
    private val getTaskUseCase: GetTaskUseCase,
) : ViewModel() {

    private val _selectedCategory = MutableStateFlow<String?>(null)
    val selectedCategory: StateFlow<String?> = _selectedCategory

    fun setCategory(category: String?) {
        _selectedCategory.value = category
    }

    // Estado para recuperar todas las tareas de una categoría específica
    @OptIn(ExperimentalCoroutinesApi::class)
    val tasksByCategoryState: StateFlow<TasksUiState> = _selectedCategory
        .flatMapLatest { category ->
            if (category != null) {
                getTaskByCategoryUseCase(category).map{ Success(it.toViewModelList()) }
            } else {
                getTaskUseCase().map { Success(it.toViewModelList()) }
            }
        }
        .catch { Error(it) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), Loading)
}