package com.example.todoapp.taskcategory.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.*
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun TaskCategoryScreen(
    taskCategoryViewModel: TaskCategoryViewModel,
    navigationController: NavHostController,
) {
    var categoryText by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Input field for category name
        TextField(
            value = categoryText,
            onValueChange = { categoryText = it },
            label = { Text("Enter Category") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Button to create category
        Button(
            onClick = {
                taskCategoryViewModel.onTaskCategoryCreated(categoryText)
                categoryText = "" // Clear the text field after creating category
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Create Category")
        }
    }
}
