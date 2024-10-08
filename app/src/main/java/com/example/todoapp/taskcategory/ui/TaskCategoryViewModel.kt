package com.example.todoapp.taskcategory.ui

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.todoapp.taskcategory.domain.AddCategoryUseCase
import com.example.todoapp.taskcategory.domain.DeleteCategoryUseCase
import com.example.todoapp.taskcategory.domain.GetCategoryUseCase
import com.example.todoapp.taskcategory.domain.UpdateCategoryUseCase
import com.example.todoapp.taskcategory.domain.model.toDomain
import com.example.todoapp.taskcategory.ui.TaskCategoryUiState.Success
import com.example.todoapp.taskcategory.ui.model.TaskCategoryModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TaskCategoryViewModel @Inject constructor(
    getCategoryUseCase: GetCategoryUseCase,
    private val addCategoryUseCase: AddCategoryUseCase,
    private val deleteCategoryUseCase: DeleteCategoryUseCase,
    private val updateCategoryUseCase: UpdateCategoryUseCase,
) : ViewModel() {

    private val _showDropDown = MutableLiveData(false)
    val showDropDown: MutableLiveData<Boolean> = _showDropDown

    private val _selectedCategory = MutableLiveData<String?>(null)

    fun setShowDropDown(show: Boolean) {
        _showDropDown.value = show
    }

    private val _showCreateDialog = MutableLiveData(false)
    val showCreateDialog: MutableLiveData<Boolean> = _showCreateDialog

    fun setShowCreateDialog(show: Boolean) {
        _showCreateDialog.value = show
    }

    fun setSelectedCategory(category: String) {
        _selectedCategory.value = category
        _showDropDown.value = !_showDropDown.value!!
    }


    // Propiedad para las categorías que carga todas las categorías, incluyendo las predeterminadas
    val categories: StateFlow<List<TaskCategoryModel>> = getCategoryUseCase()
        .map { it }
        .catch { emit(emptyList()) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val uiState: StateFlow<TaskCategoryUiState> = getCategoryUseCase()
        .map { Success(it) }
        .catch { exception -> TaskCategoryUiState.Error(exception) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = TaskCategoryUiState.Loading
        )

    fun onTaskCategoryCreated(category: String) {
        viewModelScope.launch {
            addCategoryUseCase(TaskCategoryModel(category = category).toDomain())
        }
    }

    fun onTaskCategoryUpdate(category: TaskCategoryModel) {
        viewModelScope.launch {
            updateCategoryUseCase(category.toDomain())
        }
    }

    fun onTaskCategoryRemove(category: TaskCategoryModel) {
        viewModelScope.launch {
            deleteCategoryUseCase(category)
        }
    }
}