package com.example.todoapp.addtasks.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.todoapp.addtasks.domain.DeleteTaskUseCase
import com.example.todoapp.addtasks.domain.UpdateTaskUseCase
import com.example.todoapp.addtasks.domain.model.toDomain
import com.example.todoapp.addtasks.ui.model.TaskModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.threeten.bp.LocalDate
import org.threeten.bp.LocalTime
import javax.inject.Inject

@HiltViewModel
open class BaseTaskViewModel @Inject constructor(
    protected val updateTaskUseCase: UpdateTaskUseCase,
    protected val deleteTaskUseCase: DeleteTaskUseCase
) : ViewModel() {

    fun updateTask(taskModel: TaskModel){
        viewModelScope.launch {
            updateTaskUseCase(taskModel.toDomain())
        }
    }

    fun onDeleted(taskModel: TaskModel) {
        viewModelScope.launch {
            deleteTaskUseCase(taskModel.toDomain())
        }
    }

    private val _temporaryDate = MutableStateFlow<LocalDate?>(null)
    val temporaryDate: StateFlow<LocalDate?> = _temporaryDate

    private val _temporaryTime = MutableStateFlow<LocalTime?>(null)
    val temporaryTime: StateFlow<LocalTime?> = _temporaryTime

    private val _showDatePicker = MutableStateFlow(false)
    val showDatePicker: StateFlow<Boolean> = _showDatePicker

    fun onShowDateDialogClick() {
        _showDatePicker.value = true
    }

    fun onHideDatePicker() {
        _showDatePicker.value = false
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