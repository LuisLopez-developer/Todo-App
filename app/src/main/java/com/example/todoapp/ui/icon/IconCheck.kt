package com.example.todoapp.ui.icon

import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.example.todoapp.R

@Composable
fun IconCheck(){
    Icon(
        painterResource(R.drawable.ic_outline_check_circle),
        contentDescription = stringResource(R.string.ic_check_circle),
        tint = colorScheme.tertiaryContainer
    )
}