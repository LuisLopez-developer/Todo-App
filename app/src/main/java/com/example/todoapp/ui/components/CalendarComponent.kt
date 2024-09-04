package com.example.todoapp.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.todoapp.R
import kotlinx.coroutines.launch
import org.threeten.bp.LocalDate
import org.threeten.bp.YearMonth
import org.threeten.bp.format.DateTimeFormatter
import kotlin.math.absoluteValue

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CalendarComponent(
    modifier: Modifier = Modifier,
    initialDate: LocalDate = LocalDate.now(),
    onDateSelected: (LocalDate) -> Unit = {},
) {
    var selectedDate by remember { mutableStateOf(initialDate) }
    var currentMonth by remember { mutableStateOf(initialDate.withDayOfMonth(1)) }

    val pagerState = rememberPagerState(
        initialPage = calculateInitialPage(initialDate),
        pageCount = { 12 * 200 }
    )
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(pagerState.currentPage) {
        val yearMonth = pageToYearMonth(pagerState.currentPage)
        currentMonth = yearMonth.atDay(1)
        selectedDate = if (selectedDate.month != currentMonth.month) {
            currentMonth.withDayOfMonth(
                minOf(
                    selectedDate.dayOfMonth,
                    currentMonth.lengthOfMonth()
                )
            )
        } else {
            selectedDate
        }
    }

    Column {
        Header(
            date = currentMonth,
            onPreviousMonth = {
                coroutineScope.launch {
                    pagerState.animateScrollToPage(pagerState.currentPage - 1)
                }
            },
            onNextMonth = {
                coroutineScope.launch {
                    pagerState.animateScrollToPage(pagerState.currentPage + 1)
                }
            }
        )
        DaysOfTheWeek()

        HorizontalPager(
            state = pagerState,
            verticalAlignment = Alignment.Top,
            modifier = Modifier.fillMaxWidth()
        ) { page ->
            DaysOfTheMonth(
                selectedDate = selectedDate,
                onDateSelected = { date ->
                    selectedDate = date
                    onDateSelected(date)
                },
                pagerState = pagerState,
                page = page
            )

        }
    }
}

fun calculateInitialPage(initialDate: LocalDate): Int {
    val yearMonth = YearMonth.from(initialDate)
    val yearOffset = 100
    val calculatedPage =
        ((yearMonth.year - (initialDate.year - yearOffset)) * 12) + (yearMonth.monthValue - 1)

    // Asegurarte de que la página esté dentro de un rango válido
    return calculatedPage.coerceIn(0, 12 * 200)
}

// Convertir la página a YearMonth para gestionar el calendario
fun pageToYearMonth(page: Int): YearMonth {
    val yearOffset = 100
    val year = (page / 12) + (LocalDate.now().year - yearOffset)
    val month = (page % 12) + 1
    return YearMonth.of(year, month)
}

@Composable
fun Header(
    date: LocalDate,
    onPreviousMonth: () -> Unit,
    onNextMonth: () -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        NavigationIcon(
            onClick = onPreviousMonth,
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
            onClick = onNextMonth,
            iconId = R.drawable.ic_round_arrow_right,
            contentDescription = "Next"
        )
    }
}


@Composable
fun DaysOfTheWeek() {
    val daysOfWeek = listOf("Dom", "Lun", "Mar", "Mie", "Jue", "Vie", "Sab")

    Row(
        modifier = Modifier.fillMaxWidth(),
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

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun DaysOfTheMonth(
    selectedDate: LocalDate,
    currentDay: LocalDate = LocalDate.now(),
    onDateSelected: (LocalDate) -> Unit,
    pagerState: PagerState,
    page: Int,
) {
    val yearMonth = pageToYearMonth(page)
    val currentMonthFirstDay = yearMonth.atDay(1)
    val daysInMonth = currentMonthFirstDay.lengthOfMonth()
    val firstDayOfWeek = (currentMonthFirstDay.dayOfWeek.value % 7)
    val numRows = ((daysInMonth + firstDayOfWeek - 1) / 7) + 1

    // Calcula el desplazamiento de la página actual desde su posición ideal
    // Se utiliza para animar la altura de la última fila de días
    // Nota: se podria crear un funcion que te devuelva eldesplazamiento al pasarle una pagina, para reutilizarla en futuro
    val pageOffset = pagerState.getOffsetFractionForPage(page).absoluteValue

    LazyColumn {
        items(numRows) { week ->
            // Ajusta la altura dinámicamente en función del desplazamiento de la página
            val height = if (week == 5) 40.dp * (1 - pageOffset) else 40.dp

            Row(
                modifier = Modifier
                    .height(height),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                for (day in 0 until 7) {
                    val dayIndex = week * 7 + day - firstDayOfWeek + 1
                    if (dayIndex in 1..daysInMonth) {
                        val date = currentMonthFirstDay.plusDays(dayIndex.toLong() - 1)
                        DayCell(
                            date = date,
                            isSelected = date == selectedDate,
                            isToday = date == currentDay,
                            onDateSelected = onDateSelected,
                            modifier = Modifier.weight(1f)
                        )
                    } else {
                        EmptyDayCell(modifier = Modifier.weight(1f))
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
    val backgroundColor by animateColorAsState(
        targetValue = when {
            isSelected -> colorScheme.primaryContainer
            isToday -> colorScheme.surfaceVariant
            else -> colorScheme.background
        }, animationSpec = tween(durationMillis = 500), label = ""
    )

    val textColor by animateColorAsState(
        targetValue = if (isSelected) colorScheme.inverseOnSurface else colorScheme.onBackground,
        label = "" // Duración de la animación
    )

    val interactionSource = remember { MutableInteractionSource() }


    CalendarCell(
        date = date,
        onDateSelected = onDateSelected,
        modifier = modifier
    ) {
        Text(
            text = date.dayOfMonth.toString(),
            color = textColor,
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