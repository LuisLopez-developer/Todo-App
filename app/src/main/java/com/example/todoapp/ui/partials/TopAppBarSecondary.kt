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
import androidx.navigation.NavHostController
import com.example.todoapp.ui.constants.StylesTopBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopAppBarSecondary(
    style: StylesTopBar,
    title: String = "Todo App",
    titleColor: Color = colorScheme.primary,
    backgroundColor: Color = colorScheme.primaryContainer,
    navController: NavHostController? = null, // Optional parameter
    onActionClick: () -> Unit = {}
) {
    val isVisibleTitle = style == StylesTopBar.SECONDARY_BACK_TITLE || style == StylesTopBar.MAIN
    val isVisibleBack = style == StylesTopBar.SECONDARY_BACK_OPTIONS || style == StylesTopBar.SECONDARY_BACK_TITLE
    val isVisibleAction = style == StylesTopBar.SECONDARY_BACK_OPTIONS

    CenterAlignedTopAppBar(
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = backgroundColor,
            titleContentColor = titleColor
        ),
        title = {
            if (isVisibleTitle) {
                Text(text = title, color = titleColor)
            }
        },
        navigationIcon = {
            if (isVisibleBack) {
                IconButton(onClick = { navController?.navigateUp() }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        tint = colorScheme.onPrimaryContainer,
                        contentDescription = "Back"
                    )
                }
            }
        },
        actions = {
            if (isVisibleAction) {
                IconButton(onClick = onActionClick) {
                    Icon(
                        imageVector = Icons.Rounded.MoreVert,
                        tint = colorScheme.onPrimaryContainer,
                        contentDescription = "More options"
                    )
                }
            }
        }
    )
}