package com.example.todoapp.taskcategory.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.*
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavHostController
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import com.example.todoapp.taskcategory.ui.model.TaskCategoryModel
import kotlinx.coroutines.flow.collect

@Composable
fun TaskCategoryScreen(
    taskCategoryViewModel: TaskCategoryViewModel,
    navigationController: NavHostController,
) {
    var categoryText by remember { mutableStateOf("") }
    val lifecycle = LocalLifecycleOwner.current.lifecycle

    // Observa el estado de UI desde el ViewModel
    val uiState by produceState<TaskCategoryUiState>(
        initialValue = TaskCategoryUiState.Loading,
        key1 = lifecycle,
        key2 = taskCategoryViewModel
    ) {
        lifecycle.repeatOnLifecycle(state = Lifecycle.State.STARTED) {
            taskCategoryViewModel.uiState.collect { value = it }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        when (uiState) {
            is TaskCategoryUiState.Error -> {
                // Muestra un mensaje de error
                Text("Error:")
            }
            TaskCategoryUiState.Loading -> {
                // Muestra un indicador de carga
                CircularProgressIndicator()
            }
            is TaskCategoryUiState.Success -> {
                // Muestra las categorías cuando el estado es de éxito
                CategoryList((uiState as TaskCategoryUiState.Success).categories)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Input field para nombre de la categoría
        TextField(
            value = categoryText,
            onValueChange = { categoryText = it },
            label = { Text("Enter Category") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Botón para crear categoría
        Button(
            onClick = {
                taskCategoryViewModel.onTaskCategoryCreated(categoryText)
                categoryText = "" // Limpia el campo de texto después de crear la categoría
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Create Category")
        }
    }
}

@Composable
fun CategoryList(categories: List<TaskCategoryModel>) {
    Column {
        categories.forEach { category ->
            Text(text = category.category, modifier = Modifier.padding(8.dp))
        }
    }
}
