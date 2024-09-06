package com.example.todoapp.ui.partials

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.rounded.MoreVert
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
    isVisibleBack: Boolean = false,
    isVisibleAction: Boolean = false,
) {
    CenterAlignedTopAppBar(
        colors = with(TopAppBarDefaults) {
            topAppBarColors(
                containerColor = backgroundColor,
                titleContentColor = titleColor,
            )
        },
        title = { Text(text = title) },
        navigationIcon = {
            if (isVisibleBack) {
                IconButton(onClick = { /* do something */ }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Localized descriptioon"
                    )
                }
            }
        },
        actions = {
            if (isVisibleAction) {
                IconButton(onClick = { /* do something */ }) {
                    Icon(
                        imageVector = Icons.Rounded.MoreVert,
                        tint = colorScheme.onPrimaryContainer,
                        contentDescription = "Localized description"
                    )
                }
            }
        }
    )
}
