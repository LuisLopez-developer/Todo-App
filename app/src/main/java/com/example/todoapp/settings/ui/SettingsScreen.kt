package com.example.todoapp.settings.ui

import android.app.Activity
import android.graphics.Paint
import android.widget.Toast
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.todoapp.R
import com.example.todoapp.R.string
import com.example.todoapp.settings.auth.ui.AuthViewModel
import com.example.todoapp.settings.auth.ui.component.SignInWithGoogle
import com.example.todoapp.ui.components.CardSettings
import com.example.todoapp.ui.navigation.DriveRoute
import com.example.todoapp.user.ui.model.UserModel

@Composable
fun SettingsScreen(
    settingsViewModel: SettingsViewModel,
    navController: NavHostController,
    authViewModel: AuthViewModel,
) {
    val context = LocalContext.current as Activity

    val userUiState by settingsViewModel.userUiState.collectAsState()

    // Inicializaciones
    val dropDownAuthMenuExpanded by settingsViewModel.dropDownExpanded.collectAsState()

    when (userUiState) {
        is UserUiState.Success -> {
            Container(
                userModel = (userUiState as UserUiState.Success).user,
                settingsViewModel = settingsViewModel,
                context = context,
                dropDownAuthMenuExpanded = dropDownAuthMenuExpanded,
                navController = navController,
                authViewModel = authViewModel
            )
        }

        is UserUiState.Empty -> {
            Container(
                userModel = null,
                settingsViewModel = settingsViewModel,
                context = context,
                dropDownAuthMenuExpanded = dropDownAuthMenuExpanded,
                navController = navController,
                authViewModel = authViewModel
            )
        }

        is UserUiState.Loading -> {
            Text("Cargando...")
        }

        is UserUiState.Error -> {
            Text("Error: ${(userUiState as UserUiState.Error).throwable.message}")
        }
    }
}

@Composable
fun Container(
    userModel: UserModel?,
    settingsViewModel: SettingsViewModel,
    context: Activity,
    dropDownAuthMenuExpanded: Boolean,
    navController: NavHostController,
    authViewModel: AuthViewModel,
) {
    Column {
        Auth(
            userModel = userModel,
            settingsViewModel = settingsViewModel,
            dropDownAuthMenuExpanded = dropDownAuthMenuExpanded,
            authViewModel = authViewModel
        )

        CardSettings(modifier = Modifier.padding(horizontal = 15.dp), enable = userModel != null, text = stringResource(string.sync_cloud), onClick = {
            if (userModel != null) {
                navController.navigate(DriveRoute(userId = userModel.id))
            } else {
                Toast.makeText(context, string.sign_in_to_sync, Toast.LENGTH_SHORT).show()
            }
        }, icon = {
            Icon(
                painter = painterResource(R.drawable.ic_outline_cloud_sync),
                contentDescription = stringResource(string.ic_sync_cloud),
                tint = colorScheme.tertiaryContainer.copy(alpha = if (userModel != null) 1f else 0.5f)
            )
        })

        Spacer(modifier = Modifier.padding(6.dp))

        CardSettings(modifier = Modifier.padding(horizontal = 15.dp), enable = true, text = stringResource(string.alerts_and_notifications), onClick = {

        }, icon = {
            Icon(
                painter = painterResource(R.drawable.ic_outline_notifications),
                contentDescription = stringResource(string.ic_notifications),
                tint = colorScheme.tertiaryContainer.copy(alpha = 1f)
            )
        })

    }
}

@Composable
fun Auth(
    userModel: UserModel?,
    settingsViewModel: SettingsViewModel,
    dropDownAuthMenuExpanded: Boolean,
    authViewModel: AuthViewModel,
) {
    val colorScheme = colorScheme

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(horizontal = 15.dp, vertical = 20.dp),
        elevation = CardDefaults.cardElevation(8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(color = colorScheme.surfaceContainer)
                .padding(15.dp)
        ) {
            if (userModel == null) {
                Text(
                    stringResource(string.sign_in),
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
                HorizontalDivider(
                    thickness = 2.dp, modifier = Modifier.padding(vertical = 10.dp)
                )
                Row(modifier = Modifier.align(Alignment.CenterHorizontally)) {

                    SignInWithGoogle(authViewModel)

                    Spacer(modifier = Modifier.padding(8.dp))
                    IconButton(onClick = { /* Iniciar sesion con GitHub */ }) {
                        Icon(
                            painter = painterResource(R.drawable.ic_github_light),
                            contentDescription = "GitHub"
                        )
                    }
                }
            } else {
                // Obtener la inicial del nombre del usuario
                val initial = userModel.name.first().uppercase()
                Row {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Canvas(
                            modifier = Modifier
                                .size(40.dp)
                                .background(colorScheme.tertiaryContainer, shape = CircleShape)
                        ) {
                            // Dibujar la inicial del nombre del usuario
                            drawIntoCanvas { canvas ->
                                val paint = Paint().apply {
                                    color = colorScheme.onTertiaryContainer.toArgb()
                                    textAlign = Paint.Align.CENTER
                                    textSize = 24.sp.toPx()
                                }
                                val x = size.width / 2
                                val y = (size.height / 2) - ((paint.descent() + paint.ascent()) / 2)
                                canvas.nativeCanvas.drawText(initial, x, y, paint)
                            }
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(text = "Hola,")
                            Text(userModel.name, fontWeight = FontWeight.Bold)
                        }
                    }
                    Box {
                        IconButton(onClick = { settingsViewModel.onShowDropDownExpanded() }) {
                            Icon(
                                painter = painterResource(R.drawable.ic_more_vert),
                                contentDescription = stringResource(string.sign_out)
                            )
                        }
                        DropdownMenu(expanded = dropDownAuthMenuExpanded,
                            onDismissRequest = { settingsViewModel.onHideDropDownExpanded() }) {
                            DropdownMenuItem(onClick = { settingsViewModel.signOut() },
                                text = { Text(stringResource(string.sign_out)) })
                        }
                    }
                }
            }
        }
    }
}