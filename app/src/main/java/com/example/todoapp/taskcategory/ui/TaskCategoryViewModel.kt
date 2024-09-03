package com.example.todoapp.taskcategory.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.todoapp.addtasks.domain.AddTaskUseCase
import com.example.todoapp.addtasks.ui.model.TaskModel
import com.example.todoapp.taskcategory.domain.AddCategoryUseCase
import com.example.todoapp.taskcategory.ui.model.TaskCategoryModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TaskCategoryViewModel @Inject constructor(
    private val addCategoryUseCase: AddCategoryUseCase
) : ViewModel() {


    fun onTaskCategoryCreated(category: String) {
        viewModelScope.launch {
            addCategoryUseCase(TaskCategoryModel(category = category))
        }

    }

    fun onTaskCategoryUpdate(category: TaskCategoryModel) {

    }

    fun onTaskCategoryRemove(category: TaskCategoryModel) {

    }
}