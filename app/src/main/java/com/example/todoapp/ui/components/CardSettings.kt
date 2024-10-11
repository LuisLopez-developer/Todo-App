package com.example.todoapp.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.todoapp.R

@Composable
fun CardSettings(
    enable: Boolean,
    onClick: () -> Unit,
    icon: @Composable (() -> Unit?)? = null,
    text: String = stringResource(R.string.sn),
    modifier: Modifier = Modifier
) {
    val alpha = if (enable) 1f else 0.5f

    Card(
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(colorScheme.surfaceContainer.copy(alpha))
                .padding(15.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                if (icon != null) {
                    icon()
                }else {
                    Icon(
                        painter = painterResource(R.drawable.ic_settings),
                        contentDescription = stringResource(R.string.ic_settings),
                        tint = colorScheme.tertiaryContainer.copy(alpha)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text,
                    color = colorScheme.onSurface.copy(alpha)
                )
            }
            Icon(
                painter = painterResource(R.drawable.ic_navigate_next),
                contentDescription = stringResource(R.string.ic_navigate_next),
                tint = colorScheme.tertiaryContainer.copy(alpha)
            )
        }
    }
}