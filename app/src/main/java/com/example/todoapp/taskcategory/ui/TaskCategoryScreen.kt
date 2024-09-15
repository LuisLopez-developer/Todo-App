package com.example.todoapp.taskcategory.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.todoapp.R
import com.example.todoapp.taskcategory.ui.model.TaskCategoryModel

@Composable
fun TaskCategoryScreen(
    taskCategoryViewModel: TaskCategoryViewModel,
) {
    // Estado para categorías seleccionadas utilizando SnapshotStateList
    val selectedCategories = remember { mutableStateListOf<TaskCategoryModel>() }

    // Estado para manejar la visibilidad del diálogo de edición
    val showEditDialog = remember { mutableStateOf(false) }

    // Estado para el texto de la categoría a editar
    val editCategoryText = remember { mutableStateOf("") }

    // Estado para manejar la confirmación de eliminación
    val showDeleteConfirmation = remember { mutableStateOf(false) }
    val categoryToDelete = remember { mutableStateOf<TaskCategoryModel?>(null) }

    // Observa el estado de UI desde el ViewModel
    val uiState by taskCategoryViewModel.uiState.collectAsState(TaskCategoryUiState.Loading)

    // Estado para manejar qué menú desplegable está expandido
    val expandedMenuCategoryId = remember { mutableStateOf<Int?>(null) }

    when (uiState) {
        is TaskCategoryUiState.Error -> {
            Text("Error:")
        }

        TaskCategoryUiState.Loading -> {
            CircularProgressIndicator()
        }

        is TaskCategoryUiState.Success -> {
            Container(
                categories = (uiState as TaskCategoryUiState.Success).categories,
                selectedCategories = selectedCategories,
                taskCategoryViewModel = taskCategoryViewModel,
                showEditDialog = showEditDialog.value,
                editCategoryText = editCategoryText.value,
                expandedMenuCategoryId = expandedMenuCategoryId.value,
                onMenuExpandChange = { id -> expandedMenuCategoryId.value = id },
                onEditCategory = { category ->
                    editCategoryText.value = category.category
                    selectedCategories.clear()
                    selectedCategories.add(category)
                    showEditDialog.value = true
                },
                onDeleteCategory = { category ->
                    categoryToDelete.value = category
                    showDeleteConfirmation.value = true
                }
            )
        }
    }

    if (showEditDialog.value) {
        EditCategoryDialog(
            categoryText = editCategoryText.value,
            onCategoryTextChange = { newText -> editCategoryText.value = newText },
            onConfirm = {
                if (selectedCategories.size == 1) {
                    val selectedCategory =
                        selectedCategories.first().copy(category = editCategoryText.value)
                    taskCategoryViewModel.onTaskCategoryUpdate(selectedCategory)
                    selectedCategories.clear() // Limpia la selección después de actualizar
                }
                showEditDialog.value = false // Cierra el diálogo
            },
            onDismiss = {
                showEditDialog.value = false // Cierra el diálogo
            }
        )
    }

    if (showDeleteConfirmation.value) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirmation.value = false },
            title = { Text("Confirmar Eliminación") },
            text = { Text("¿Estás seguro de que deseas eliminar esta categoría? Esto también eliminará las tareas asociadas.") },
            confirmButton = {
                Button(onClick = {
                    categoryToDelete.value?.let { category ->
                        taskCategoryViewModel.onTaskCategoryRemove(category)
                        categoryToDelete.value = null
                    }
                    showDeleteConfirmation.value = false
                }) {
                    Text("Delete")
                }
            },
            dismissButton = {
                Button(onClick = { showDeleteConfirmation.value = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun Container(
    categories: List<TaskCategoryModel>,
    selectedCategories: SnapshotStateList<TaskCategoryModel>,
    taskCategoryViewModel: TaskCategoryViewModel,
    showEditDialog: Boolean,
    editCategoryText: String,
    expandedMenuCategoryId: Int?,
    onMenuExpandChange: (Int?) -> Unit,
    onEditCategory: (TaskCategoryModel) -> Unit,
    onDeleteCategory: (TaskCategoryModel) -> Unit
) {
    CategoryList(
        categories = categories,
        selectedCategories = selectedCategories,
        expandedMenuCategoryId = expandedMenuCategoryId,
        onMenuExpandChange = onMenuExpandChange,
        onEditCategory = onEditCategory,
        onDeleteCategory = onDeleteCategory
    )
}

@Composable
fun CategoryList(
    categories: List<TaskCategoryModel>,
    selectedCategories: SnapshotStateList<TaskCategoryModel>,
    expandedMenuCategoryId: Int?,
    onMenuExpandChange: (Int?) -> Unit,
    onEditCategory: (TaskCategoryModel) -> Unit,
    onDeleteCategory: (TaskCategoryModel) -> Unit
) {
    LazyColumn(contentPadding = PaddingValues(horizontal = 15.dp, vertical = 10.dp)) {
        items(categories) { category ->

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp)
            ) {
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier
                        .padding(10.dp)

                ) {
                    Text(
                        text = category.category.replaceFirstChar { it.uppercase() },
                        modifier = Modifier.weight(1f)
                    )

                    Box {
                        Icon(
                            painter = painterResource(R.drawable.ic_more_vert),
                            contentDescription = "",
                            Modifier.clickable {
                                // Expandir solo el menú de la categoría seleccionada
                                if (expandedMenuCategoryId == category.id) {
                                    onMenuExpandChange(null) // Cerrar si ya está abierto
                                } else {
                                    onMenuExpandChange(category.id) // Abrir el menú
                                }
                            }
                        )

                        DropdownMenu(
                            expanded = expandedMenuCategoryId == category.id, // Mostrar solo si coincide el ID
                            onDismissRequest = { onMenuExpandChange(null) }
                        ) {
                            DropdownMenuItem(
                                text = { Text(text = stringResource(R.string.dw_edit)) },
                                onClick = {
                                    onEditCategory(category)
                                    onMenuExpandChange(null)
                                }
                            )
                            DropdownMenuItem(
                                text = { Text(text = stringResource(R.string.dw_delete)) },
                                onClick = {
                                    onDeleteCategory(category)
                                    onMenuExpandChange(null)
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun EditCategoryDialog(
    categoryText: String,
    onCategoryTextChange: (String) -> Unit,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
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

