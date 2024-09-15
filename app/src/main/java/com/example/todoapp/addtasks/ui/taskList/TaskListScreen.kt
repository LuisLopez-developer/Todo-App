package com.example.todoapp.addtasks.ui.taskList

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavHostController
import com.example.todoapp.R
import com.example.todoapp.addtasks.ui.TasksUiState
import com.example.todoapp.addtasks.ui.components.TaskItemComponent
import com.example.todoapp.addtasks.ui.model.TaskModel
import com.example.todoapp.holidays.ui.HolidaysViewModel
import com.example.todoapp.holidays.ui.model.HolidayModel
import com.example.todoapp.taskcategory.ui.TaskCategoryViewModel
import com.example.todoapp.taskcategory.ui.model.TaskCategoryModel

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

    val selectedCategory by taskListViewModel.selectedCategory.collectAsState()
    val categories by taskCategoryViewModel.categories.collectAsState(emptyList())

    when (uiStateByDate) {
        is TasksUiState.Loading -> {
            CircularProgressIndicator()
        }

        is TasksUiState.Success -> {
            Container(
                tasks = (uiStateByDate as TasksUiState.Success).tasks,
                holidays = holidays,
                selectedCategory = selectedCategory,
                categories = categories,
                taskListViewModel = taskListViewModel
            )
        }

        is TasksUiState.Error -> {
            Text(text = "Error: ")
        }
    }

}

@Composable
fun Container(
    tasks: List<TaskModel>,
    holidays: List<HolidayModel>,
    selectedCategory: Int?,
    categories: List<TaskCategoryModel>,
    taskListViewModel: TaskListViewModel,
) {

    Box(modifier = Modifier.fillMaxSize()) {
        Column {
            CategorySelector(
                selectedCategory = selectedCategory,
                onCategorySelected = { category ->
                    taskListViewModel.setCategory(category?.id)
                },
                categories = categories
            )

            Box(Modifier.padding(horizontal = 15.dp)) {
                List(
                    tasks = tasks
                )
            }

        }
    }
}

@Composable
fun CategorySelector(
    selectedCategory: Int?,
    onCategorySelected: (TaskCategoryModel?) -> Unit,
    categories: List<TaskCategoryModel>,
) {
    var expanded by remember { mutableStateOf(false) }

    var selectedItem by remember { mutableStateOf(selectedCategory) }

    LazyRow(
        contentPadding = PaddingValues(horizontal = 15.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {

        item {
            CategoryItem(
                text = stringResource(R.string.list_all),
                isSelected = selectedItem == null,
                onClick = {
                    selectedItem = null
                    onCategorySelected(null)
                    expanded = false
                }
            )
        }

        items(categories) { category ->
            CategoryItem(
                text = category.category,
                isSelected = selectedItem == category.id,
                onClick = {
                    selectedItem = category.id
                    onCategorySelected(category)
                    expanded = false
                }
            )
        }
    }
}

@Composable
fun CategoryItem(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
) {
    // Convierte la primera letra a mayúscula
    val capitalizedText = text.replaceFirstChar { it.uppercase() }

    Text(
        text = capitalizedText,
        modifier = Modifier
            .clickable { onClick() }
            .background(
                if (isSelected) {
                    colorScheme.tertiaryContainer
                } else {
                    colorScheme.tertiaryContainer.copy(0.4f)
                },
                shape = RoundedCornerShape(20.dp)
            )
            .padding(horizontal = 24.dp, vertical = 6.dp),
        color = if (isSelected) {
            colorScheme.onTertiaryContainer
        } else {
            colorScheme.inverseSurface
        }
    )
}

@Composable
fun List(tasks: List<TaskModel>) {
    LazyColumn {
        items(tasks) { task ->
            TaskItemComponent(
                text = task.task,
                checked = !task.selected,
                onClick = {
                    //navigationController.navigate(EditTaskRoute(id = task.id))
                },
                onLongPress = {
                    //taskViewModel.onItemRemove(task)
                },
                onCheckBoxChange = { isChecked ->
                    //taskViewModel.onCheckBox(task.copy(selected = isChecked))
                }
            )
        }
    }
}