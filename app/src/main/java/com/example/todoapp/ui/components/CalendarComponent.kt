package com.example.todoapp.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.todoapp.R
import org.threeten.bp.LocalDate
import org.threeten.bp.format.DateTimeFormatter

@Composable
fun CalendarComponent(
    modifier: Modifier = Modifier,
    initialDate: LocalDate = LocalDate.now(),
    onDateSelected: (LocalDate) -> Unit = {},
) {
    var selectedDate by remember { mutableStateOf(initialDate) }
    val currentDay = remember { LocalDate.now() }
    var currentMonth by remember { mutableStateOf(selectedDate.withDayOfMonth(1)) }

    // Handle month change
    fun handleMonthChange(newMonth: LocalDate) {
        // Calculate the new selected date
        val newDay = if (selectedDate.dayOfMonth <= newMonth.lengthOfMonth()) {
            selectedDate.withMonth(newMonth.monthValue).withYear(newMonth.year)
        } else {
            newMonth.withDayOfMonth(newMonth.lengthOfMonth())
        }

        selectedDate = newDay
        currentMonth = newMonth
    }

    // Update selectedDate and notify parent when date is selected
    fun handleDateSelected(date: LocalDate) {
        selectedDate = date
        onDateSelected(date)
    }

    Column(
        modifier = modifier
            .padding(bottom = 20.dp)
    ) {
        Header(currentMonth, onPreviousMonth = {
            handleMonthChange(currentMonth.minusMonths(1))
        }, onNextMonth = {
            handleMonthChange(currentMonth.plusMonths(1))
        })

        Content(
            currentMonthFirstDay = currentMonth,
            selectedDate = selectedDate,
            currentDay = currentDay,
            onDateSelected = { date -> handleDateSelected(date) }
        )
    }
}

@Composable
fun Header(
    date: LocalDate,
    onPreviousMonth: () -> Unit,
    onNextMonth: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        NavigationIcon(
            onClick = onPreviousMonth,  // Pass the callback for previous month
            iconId = R.drawable.ic_round_arrow_left,
            contentDescription = "Previous"
        )
        Text(
            text = formatDate(date),
            modifier = Modifier.weight(1f),
            textAlign = TextAlign.Center,
            fontSize = 20.sp
        )
        NavigationIcon(
            onClick = onNextMonth,  // Pass the callback for next month
            iconId = R.drawable.ic_round_arrow_right,
            contentDescription = "Next"
        )
    }
}

@Composable
fun Content(
    currentMonthFirstDay: LocalDate,
    selectedDate: LocalDate,
    currentDay: LocalDate,
    onDateSelected: (LocalDate) -> Unit,
) {
    DaysOfTheWeek()
    DaysOfTheMonth(
        currentMonthFirstDay = currentMonthFirstDay,
        selectedDate = selectedDate,
        currentDay = currentDay,
        onDateSelected = onDateSelected
    )
}

@Composable
fun DaysOfTheWeek() {
    val daysOfWeek = remember { listOf("Dom", "Lun", "Mar", "Mie", "Jue", "Vie", "Sab") }

    Row(
        modifier = Modifier
            .fillMaxWidth(),
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
fun DaysOfTheMonth(
    currentMonthFirstDay: LocalDate,
    selectedDate: LocalDate,
    currentDay: LocalDate,
    onDateSelected: (LocalDate) -> Unit,
) {
    val daysInMonth = currentMonthFirstDay.lengthOfMonth()
    val firstDayOfWeek = remember(currentMonthFirstDay) { currentMonthFirstDay.dayOfWeek.value % 7 }
    val numRows = remember(daysInMonth, firstDayOfWeek) { (daysInMonth + firstDayOfWeek + 6) / 7 }

    LazyColumn {
        items(numRows) { week ->
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                for (day in 0 until 7) {
                    if (week == 0 && day < firstDayOfWeek || (week * 7 + day - firstDayOfWeek + 1) > daysInMonth) {
                        EmptyDayCell(modifier = Modifier.weight(1f))
                    } else {
                        val date =
                            currentMonthFirstDay.plusDays((week * 7 + day - firstDayOfWeek).toLong())
                        DayCell(
                            date = date,
                            isSelected = date == selectedDate,
                            isToday = date == currentDay,
                            onDateSelected = onDateSelected,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun NavigationIcon(onClick: () -> Unit, iconId: Int, contentDescription: String) {
    IconButton(onClick = onClick) {
        Icon(
            painter = painterResource(id = iconId),
            contentDescription = contentDescription,
            modifier = Modifier.size(40.dp)
        )
    }
}

@Composable
fun formatDate(date: LocalDate): String {
    val dateFormatter = remember {
        DateTimeFormatter.ofPattern("MMMM yyyy", java.util.Locale("es", "PE"))
    }
    return date.format(dateFormatter).uppercase()
}

@Composable
fun CalendarCell(
    modifier: Modifier = Modifier,
    date: LocalDate?,
    onDateSelected: ((LocalDate) -> Unit)? = null,
    content: @Composable (Modifier) -> Unit,
) {
    Box(
        modifier = modifier
            .size(40.dp)
            .clickable { date?.let { onDateSelected?.invoke(it) } },
        contentAlignment = Alignment.Center
    ) {
        content(modifier)
    }
}

@Composable
fun EmptyDayCell(modifier: Modifier = Modifier) {
    CalendarCell(
        date = null,
        modifier = modifier
    ) {}
}

@Composable
fun DayCell(
    date: LocalDate,
    isSelected: Boolean,
    isToday: Boolean,
    onDateSelected: (LocalDate) -> Unit,
    modifier: Modifier = Modifier,
) {
    val backgroundColor by rememberUpdatedState(
        when {
            isSelected -> colorScheme.primaryContainer
            isToday -> colorScheme.surfaceVariant
            else -> colorScheme.background
        }
    )

    // Crear un InteractionSource personalizado que no realice ninguna acción
    val interactionSource = remember { MutableInteractionSource() }

    CalendarCell(
        date = date,
        onDateSelected = onDateSelected,
        modifier = modifier
    ) {
        Text(
            text = date.dayOfMonth.toString(),
            color = if (isSelected) colorScheme.inverseOnSurface else colorScheme.onBackground,
            modifier = Modifier
                .background(
                    color = backgroundColor,
                    shape = CircleShape
                )
                .size(30.dp)
                .wrapContentSize(align = Alignment.Center)
                .clickable(
                    interactionSource = interactionSource,
                    indication = null
                ) {
                    onDateSelected(date)
                }
        )
    }
}