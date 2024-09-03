package com.example.todoapp.taskcategory.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavHostController
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role.Companion.Checkbox
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

    // Estado para categorías seleccionadas utilizando SnapshotStateList
    val selectedCategories = remember { mutableStateListOf<TaskCategoryModel>() }

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
                CategoryList(
                    categories = (uiState as TaskCategoryUiState.Success).categories,
                    selectedCategories = selectedCategories // Pasar el parámetro correcto
                )
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

        // Botón para eliminar categorías seleccionadas
        Button(
            onClick = {
                selectedCategories.forEach { taskCategoryViewModel.onTaskCategoryRemove(it) }
                selectedCategories.clear() // Limpia la selección después de eliminar
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = selectedCategories.isNotEmpty() // Solo habilitado si hay categorías seleccionadas
        ) {
            Text("Delete category/s")
        }
    }
}

@Composable
fun CategoryList(categories: List<TaskCategoryModel>,
                 selectedCategories: SnapshotStateList<TaskCategoryModel> // Cambiado a SnapshotStateList
) {
    Column {
        categories.forEach { category ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                // Checkbox para seleccionar la categoría
                Checkbox(
                    checked = selectedCategories.contains(category),
                    onCheckedChange = { isChecked ->
                        if (isChecked) {
                            selectedCategories.add(category)
                        } else {
                            selectedCategories.remove(category)
                        }
                    }
                )
                Text(
                    text = category.category,
                    modifier = Modifier
                        .padding(start = 8.dp)
                        .weight(1f)
                )
            }
        }
    }
}