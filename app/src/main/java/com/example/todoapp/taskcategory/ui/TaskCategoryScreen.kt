package com.example.todoapp.taskcategory.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavHostController
import com.example.todoapp.taskcategory.ui.model.TaskCategoryModel

@Composable
fun TaskCategoryScreen(
    taskCategoryViewModel: TaskCategoryViewModel,
    navigationController: NavHostController,
) {
    var categoryText by remember { mutableStateOf("") }
    val lifecycle = LocalLifecycleOwner.current.lifecycle

    // Estado para categorías seleccionadas utilizando SnapshotStateList
    val selectedCategories = remember { mutableStateListOf<TaskCategoryModel>() }

    // Estado para manejar la visibilidad del diálogo de edición
    var showEditDialog by remember { mutableStateOf(false) }

    // Estado para el texto de la categoría a editar
    var editCategoryText by remember { mutableStateOf("") }

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
                Text("Error:")
            }

            TaskCategoryUiState.Loading -> {
                CircularProgressIndicator()
            }

            is TaskCategoryUiState.Success -> {
                CategoryList(
                    categories = (uiState as TaskCategoryUiState.Success).categories,
                    selectedCategories = selectedCategories // Pasar el parámetro correcto
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        TextField(
            value = categoryText,
            onValueChange = { categoryText = it },
            label = { Text("Enter Category") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                taskCategoryViewModel.onTaskCategoryCreated(categoryText)
                categoryText = "" // Limpia el campo de texto después de crear la categoría
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Create Category")
        }

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

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                if (selectedCategories.size == 1) {
                    editCategoryText =
                        selectedCategories.first().category // Obtén el texto de la categoría seleccionada
                    showEditDialog = true // Mostrar el diálogo de edición
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = selectedCategories.size == 1 // Solo habilitado si hay exactamente una categoría seleccionada
        ) {
            Text("Edit Selected Category")
        }
    }

    if (showEditDialog) {
        EditCategoryDialog(
            categoryText = editCategoryText,
            onCategoryTextChange = { editCategoryText = it },
            onConfirm = {
                if (selectedCategories.size == 1) {
                    val selectedCategory =
                        selectedCategories.first().copy(category = editCategoryText)
                    taskCategoryViewModel.onTaskCategoryUpdate(selectedCategory)
                    selectedCategories.clear() // Limpia la selección después de actualizar
                }
                showEditDialog = false // Cierra el diálogo
            },
            onDismiss = {
                showEditDialog = false // Cierra el diálogo
            }
        )
    }
}

@Composable
fun CategoryList(
    categories: List<TaskCategoryModel>,
    selectedCategories: SnapshotStateList<TaskCategoryModel> // Cambiado a SnapshotStateList
) {
    Column {
        categories.forEach { category ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
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

@Composable
fun EditCategoryDialog(
    categoryText: String,
    onCategoryTextChange: (String) -> Unit,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit Category") },
        text = {
            TextField(
                value = categoryText,
                onValueChange = onCategoryTextChange,
                label = { Text("Category Name") }
            )
        },
        confirmButton = {
            Button(onClick = onConfirm) {
                Text("Confirm")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
