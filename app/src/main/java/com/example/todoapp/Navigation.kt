package com.example.todoapp

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavHostController
import com.example.todoapp.navigation.Routes.Pantalla2
import com.example.todoapp.navigation.Routes.Pantalla3

@Composable
fun Screen1(navigationController: NavHostController) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color.Red)
    ) {
        Text(
            text = "Pantalla 1",
            modifier = Modifier
                .align(Alignment.Center)
                .clickable { navigationController.navigate(Pantalla2.route) })
    }
}

@Composable
fun Screen2(navigationController: NavHostController) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color.Green)
    ) {
        Text(text = "Pantalla 2", modifier = Modifier
            .align(Alignment.Center)
            .clickable {
                navigationController.navigate(
                    Pantalla3.route
                )
            })
    }
}

@Composable
fun Screen3(navigationController: NavHostController, id: Int) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color.Blue)
    ) {
        Text(text = id.toString(), modifier = Modifier.align(Alignment.Center))
    }
}
