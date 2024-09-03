package com.example.todoapp.taskcategory.ui

import androidx.lifecycle.ViewModel
import com.example.todoapp.taskcategory.ui.model.TaskCategoryModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class TaskCategoryViewModel @Inject constructor(): ViewModel()  {

    fun onTaskCategoryCreated(category: String){

    }

    fun onTaskCategoryUpdate(category: TaskCategoryModel){

    }

    fun onTaskCategoryRemove(category: TaskCategoryModel){

    }
}