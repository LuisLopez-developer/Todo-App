package com.example.todoapp.taskcategory.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
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
    val showCreateDialog by taskCategoryViewModel.showCreateDialog.observeAsState(false)

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
                },
                categoryToDelete = categoryToDelete.value, // Añadir la categoría que se desea eliminar
                showDeleteConfirmation = showDeleteConfirmation.value, // Añadir estado de confirmación de eliminación
                onDismissEditDialog = {
                    showEditDialog.value = false
                }, // Añadir acción para cerrar el diálogo de edición
                onConfirmEditDialog = { newText ->
                    editCategoryText.value = newText
                }, // Acción para confirmar la edición
                onDismissDeleteDialog = {
                    showDeleteConfirmation.value = false
                }, // Añadir acción para cerrar el diálogo de eliminación
                onConfirmDelete = {
                    categoryToDelete.value?.let { taskCategoryViewModel.onTaskCategoryRemove(it) }
                    categoryToDelete.value = null
                },
                showCreateDialog = showCreateDialog
            )
        }

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
    onDeleteCategory: (TaskCategoryModel) -> Unit,
    categoryToDelete: TaskCategoryModel?,  // Agregado para manejar la categoría a eliminar
    showDeleteConfirmation: Boolean,      // Agregado para manejar la confirmación de eliminación
    onDismissEditDialog: () -> Unit,      // Agregado para cerrar el diálogo de edición
    onConfirmEditDialog: (String) -> Unit, // Agregado para confirmar la edición
    onDismissDeleteDialog: () -> Unit,    // Agregado para cerrar el diálogo de eliminación
    onConfirmDelete: () -> Unit,           // Agregado para confirmar la eliminación
    showCreateDialog: Boolean
) {
    Box(modifier = Modifier.fillMaxSize()) {
        CategoryList(
            categories = categories,
            selectedCategories = selectedCategories,
            expandedMenuCategoryId = expandedMenuCategoryId,
            onMenuExpandChange = onMenuExpandChange,
            onEditCategory = onEditCategory,
            onDeleteCategory = onDeleteCategory
        )

        // Botón flotante

        FloatingActionButton(
            onClick = {
                taskCategoryViewModel.setShowCreateDialog(true)
            },
            modifier = Modifier
                .align(Alignment.BottomEnd) // Posiciona el botón en la parte inferior derecha
                .padding(16.dp) // Añade un margen desde el borde de la pantalla
        ) {
            Icon(Icons.Filled.Add, contentDescription = "Botón para agregar tareas")
        }


        // Diálogo de edición
        if (showEditDialog) {
            EditCategoryDialog(
                categoryText = editCategoryText,
                onCategoryTextChange = { newText -> onConfirmEditDialog(newText) },
                onConfirm = {
                    if (selectedCategories.size == 1) {
                        val selectedCategory =
                            selectedCategories.first().copy(category = editCategoryText)
                        taskCategoryViewModel.onTaskCategoryUpdate(selectedCategory)
                        selectedCategories.clear() // Limpia la selección después de actualizar
                    }
                    onDismissEditDialog() // Cierra el diálogo
                },
                onDismiss = { onDismissEditDialog() }
            )
        }

        // Diálogo de confirmación de eliminación
        if (showDeleteConfirmation) {
            AlertDialog(
                onDismissRequest = { onDismissDeleteDialog() },
                title = { Text("Confirmar Eliminación") },
                text = { Text("¿Estás seguro de que deseas eliminar esta categoría? Esto también eliminará las tareas asociadas.") },
                confirmButton = {
                    Button(onClick = {
                        onConfirmDelete()
                        onDismissDeleteDialog()
                    }) {
                        Text("Eliminar")
                    }
                },
                dismissButton = {
                    Button(onClick = { onDismissDeleteDialog() }) {
                        Text("Cancelar")
                    }
                }
            )
        }

        if (showCreateDialog) {
            CreateCategoryDialog(taskCategoryViewModel)
        }
    }
}

@Composable
fun CategoryList(
    categories: List<TaskCategoryModel>,
    selectedCategories: SnapshotStateList<TaskCategoryModel>,
    expandedMenuCategoryId: Int?,
    onMenuExpandChange: (Int?) -> Unit,
    onEditCategory: (TaskCategoryModel) -> Unit,
    onDeleteCategory: (TaskCategoryModel) -> Unit,
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

@Composable
fun CreateCategoryDialog(
    taskCategoryViewModel: TaskCategoryViewModel,
) {
    var text by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = { taskCategoryViewModel.setShowCreateDialog(false) },
        title = { Text("Edit Category") },
        text = {
            TextField(
                value = text,
                onValueChange = {
                    text = it
                },
                label = { Text("Category Name") }
            )
        },
        confirmButton = {
            Button(onClick = {
                taskCategoryViewModel.onTaskCategoryCreated(text)
                taskCategoryViewModel.setShowCreateDialog(false)
            }) {
                Text("Confirm")
            }
        },
        dismissButton = {
            Button(onClick = { taskCategoryViewModel.setShowCreateDialog(false) }) {
                Text("Cancel")
            }
        }
    )
}