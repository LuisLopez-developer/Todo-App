package com.example.todoapp.ui.partials

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopAppBar(
    title: String = "Todo App",
    titleColor: Color = colorScheme.onPrimaryContainer,
    backgroundColor: Color = colorScheme.primaryContainer,
    navigationIcon: @Composable () -> Unit = {},
    actions: @Composable RowScope.() -> Unit = {}
    ) {
    CenterAlignedTopAppBar(
        colors = with(TopAppBarDefaults) {
            topAppBarColors(
                containerColor = backgroundColor,
                titleContentColor = titleColor,
            )
        },
        title = { Text(text = title) },
        navigationIcon = navigationIcon,
        actions = actions
    )
}
