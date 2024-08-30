package com.example.todoapp.ui.components

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.todoapp.R
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun CalendarComponent(
    modifier: Modifier = Modifier,
    initialDate: LocalDate = LocalDate.now(),
    onDateSelected: (LocalDate) -> Unit = {},
) {
    // Estado de la fecha seleccionada en el calendario
    var selectedDate by remember { mutableStateOf(initialDate) }

    // Calcula el primer día del mes actual basado en la fecha seleccionada
    val currentMonthFirstDay by remember(selectedDate) {
        mutableStateOf(
            selectedDate.withDayOfMonth(
                1
            )
        )
    }

    Column(modifier = modifier) {
        // Encabezado del calendario que muestra el mes y año actual y permite navegar entre meses
        HeaderCalendar(
            date = selectedDate,
            onPreviousClick = { selectedDate = selectedDate.minusMonths(1).withDayOfMonth(1) },
            onNextClick = { selectedDate = selectedDate.plusMonths(1).withDayOfMonth(1) }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // La altura del contenedor del calendario se ajustará dinámicamente
        CalendarBody(
            currentMonthFirstDay = currentMonthFirstDay,
            selectedDate = selectedDate,
            onDateSelected = { date ->
                selectedDate = date
                onDateSelected(date)
            },
            modifier = Modifier.wrapContentHeight() // Ajusta la altura del contenedor del calendario
        )
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun HeaderCalendar(
    date: LocalDate,
    onPreviousClick: () -> Unit,
    onNextClick: () -> Unit,
) {
    // Formato para mostrar el mes y año en el encabezado
    val dateFormatter = remember { DateTimeFormatter.ofPattern("MMMM yyyy") }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        IconButton(onClick = onPreviousClick) {
            Icon(
                painter = painterResource(id = R.drawable.ic_round_arrow_left),
                contentDescription = "Previous",
                modifier = Modifier.size(40.dp)
            )
        }

        // Muestra el mes y año formateado
        Text(
            text = date.format(dateFormatter),
            modifier = Modifier.weight(1f),
            textAlign = TextAlign.Center,
            fontSize = 20.sp
        )

        IconButton(onClick = onNextClick) {
            Image(
                painter = painterResource(id = R.drawable.ic_round_arrow_right),
                contentDescription = "Next",
                modifier = Modifier.size(40.dp)
            )
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun CalendarBody(
    modifier: Modifier = Modifier,
    currentMonthFirstDay: LocalDate,
    selectedDate: LocalDate,
    onDateSelected: (LocalDate) -> Unit = {}
) {
    val daysOfWeek = remember { listOf("Dom", "Lun", "Mar", "Mie", "Jue", "Vie", "Sab") }
    val daysInMonth = currentMonthFirstDay.lengthOfMonth() // Número total de días en el mes
    val firstDayOfWeek =
        remember(currentMonthFirstDay) { currentMonthFirstDay.dayOfWeek.value % 7 } // Determina el primer día de la semana del mes actual

    Column(modifier = modifier) {
        // Encabezado con los días de la semana
        DaysOfWeekHeader(daysOfWeek)

        // Tamaño y altura de las celdas del calendario
        val daySize = 40.dp
        val rowHeight = daySize + 8.dp
        // Calcula el número de filas necesarias para mostrar todos los días del mes
        val numRows =
            remember(daysInMonth, firstDayOfWeek) { (daysInMonth + firstDayOfWeek + 6) / 7 }

        // Utiliza LazyColumn para mejorar el rendimiento en la generación de filas de días
        LazyColumn {
            items(numRows) { week ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(rowHeight),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    for (day in 0 until 7) {
                        // Crea celdas vacías para los días que no pertenecen al mes actual
                        if (week == 0 && day < firstDayOfWeek || (week * 7 + day - firstDayOfWeek + 1) > daysInMonth) {
                            EmptyDayCell(
                                modifier = Modifier
                                    .weight(1f)
                                    .size(daySize)
                            )
                        } else {
                            // Crea celdas con los días del mes actual
                            val date =
                                currentMonthFirstDay.plusDays((week * 7 + day - firstDayOfWeek).toLong())
                            DayCell(
                                date = date,
                                isSelected = date == selectedDate,
                                onDateSelected = onDateSelected,
                                modifier = Modifier
                                    .weight(1f)
                                    .size(daySize)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DaysOfWeekHeader(daysOfWeek: List<String>) {
    // Crea un encabezado de los días de la semana
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(40.dp), // Asegura que el encabezado tenga una altura consistente
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        daysOfWeek.forEach { day ->
            Text(
                text = day,
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun EmptyDayCell(modifier: Modifier = Modifier) {
    // Representa una celda vacía en el calendario
    Box(modifier = modifier)
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun DayCell(
    date: LocalDate,
    isSelected: Boolean,
    onDateSelected: (LocalDate) -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .padding(4.dp)
            .background(
                if (isSelected) Color.Blue else Color.Transparent,
                shape = CircleShape
            )
            .clickable { onDateSelected(date) },
        contentAlignment = Alignment.Center
    ) {
        // Muestra el número del día del mes actual
        Text(
            text = date.dayOfMonth.toString(),
            color = if (isSelected) Color.White else Color.Black
        )
    }
}