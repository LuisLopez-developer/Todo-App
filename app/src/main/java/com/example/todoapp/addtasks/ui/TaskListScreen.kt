package com.example.todoapp.addtasks.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavHostController
import com.example.todoapp.addtasks.ui.model.TaskModel
import com.example.todoapp.addtasks.ui.taskList.TaskListViewModel
import com.example.todoapp.holidays.ui.HolidaysViewModel
import com.example.todoapp.taskcategory.ui.TaskCategoryViewModel

@Composable
fun TaskListScreen(
    taskListViewModel: TaskListViewModel,
    taskCategoryViewModel: TaskCategoryViewModel,
    navigationController: NavHostController,
    holidaysViewModel: HolidaysViewModel,
) {
    val lifecycle = LocalLifecycleOwner.current.lifecycle

    val uiStateByDate by produceState<TasksUiState>(
        initialValue = TasksUiState.Loading,
        key1 = lifecycle,
        key2 = taskListViewModel
    ) {
        lifecycle.repeatOnLifecycle(state = Lifecycle.State.STARTED) {
            taskListViewModel.tasksByCategoryState.collect { value = it }
        }
    }

    //val taskDates by taskViewModel.taskDatesFlow.collectAsState(emptyList())
    val holidays by holidaysViewModel.holidays.collectAsState(emptyList())

    val taskByCategoryState by taskListViewModel.tasksByCategoryState.collectAsState()
    val selectedCategory by taskListViewModel.selectedCategory.collectAsState()
    val categories by taskCategoryViewModel.categories.collectAsState(emptyList())

    Column {
        // Selector de categoría
        CategorySelector(
            selectedCategory = selectedCategory,
            onCategorySelected = { category ->
                taskListViewModel.setCategory(category)
            },
            categories = categories
        )

        // Gestión del estado de las tareas por categoría
        when (taskByCategoryState) {
            is TasksUiState.Loading -> {
                CircularProgressIndicator()
            }

            is TasksUiState.Success -> {
                val tasks = (taskByCategoryState as TasksUiState.Success).tasks
                LazyColumn {
                    items(tasks) { task ->
                        TaskItem(task = task, onClick = { })
                    }
                }
            }

            is TasksUiState.Error -> {
                Text(text = "Error: ")
            }
        }
    }
}


@Composable
fun TaskItem(task: TaskModel, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .clickable(onClick = onClick)
            .padding(16.dp)
    ) {
        Text(text = task.task) // Muestra el texto de la tarea
    }
}

@Composable
fun CategorySelector(
    selectedCategory: String?,
    onCategorySelected: (String) -> Unit,
    categories: List<String>,
) {
    var expanded by remember { mutableStateOf(false) }

    // Estado para almacenar la categoría seleccionada
    var selectedItem by remember { mutableStateOf(selectedCategory) }

    Box(modifier = Modifier.padding(16.dp)) {
        // Botón que muestra la categoría seleccionada o un texto predeterminado
        Text(
            text = selectedItem ?: "Selecciona una categoría",
            modifier = Modifier
                .clickable { expanded = !expanded }
                .padding(16.dp)
        )

        // Mostrar el menú desplegable
        if (expanded) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .background(MaterialTheme.colorScheme.surface)
                    .border(1.dp, MaterialTheme.colorScheme.onSurface)
            ) {
                categories.forEach { category ->
                    Text(
                        text = category,
                        modifier = Modifier
                            .clickable {
                                selectedItem = category
                                onCategorySelected(category)
                                expanded = false
                            }
                            .padding(8.dp)
                    )
                }
            }
        }
    }
}


