package com.example.todoapp

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavHostController
import com.example.todoapp.ui.navigation.Routes.Pantalla2

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

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun Screen2(navigationController: NavHostController) {
    val pagerState = rememberPagerState(pageCount = { 10 })
    HorizontalPager(state = pagerState) { page ->
        Text(text = "Pagina: $page", modifier = Modifier.fillMaxWidth())
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
