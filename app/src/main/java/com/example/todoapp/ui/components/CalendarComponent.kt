package com.example.todoapp.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.todoapp.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.threeten.bp.LocalDate
import org.threeten.bp.YearMonth
import org.threeten.bp.format.DateTimeFormatter
import kotlin.math.absoluteValue

@Composable
fun CalendarComponent(
    modifier: Modifier = Modifier,
    initialDate: LocalDate = LocalDate.now(),
    onDateSelected: (LocalDate) -> Unit = {},
    showAdjacentMonthDays: Boolean = true,
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
        selectedDate = selectedDate.takeIf { it.month == currentMonth.month }
            ?: currentMonth.withDayOfMonth(
                minOf(
                    selectedDate.dayOfMonth,
                    currentMonth.lengthOfMonth()
                )
            )
    }

    Column(modifier = modifier) {
        Header(
            date = currentMonth,
            onPreviousMonth = { coroutineScope.launch { pagerState.animateScrollToPage(pagerState.currentPage - 1) } },
            onNextMonth = { coroutineScope.launch { pagerState.animateScrollToPage(pagerState.currentPage + 1) } }
        )
        DaysOfTheWeek()

        HorizontalPager(state = pagerState, verticalAlignment = Alignment.Top) { page ->
            DaysOfTheMonth(
                selectedDate = selectedDate,
                onDateSelected = {
                    selectedDate = it
                    onDateSelected(it)
                },
                showAdjacentMonthDays = showAdjacentMonthDays,
                pagerState = pagerState,
                page = page
            )
        }
    }
}

fun calculateInitialPage(initialDate: LocalDate): Int {
    val yearMonth = YearMonth.from(initialDate)
    return (yearMonth.year - LocalDate.now().year + 100) * 12 + yearMonth.monthValue - 1
}

// Convertir la página a YearMonth para gestionar el calendario
fun pageToYearMonth(page: Int): YearMonth {
    val year = (page / 12) + (LocalDate.now().year - 100)
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

@Composable
fun DaysOfTheMonth(
    selectedDate: LocalDate,
    currentDay: LocalDate = LocalDate.now(),
    onDateSelected: (LocalDate) -> Unit,
    pagerState: PagerState,
    page: Int,
    showAdjacentMonthDays: Boolean = false,
) {
    // Calculamos el YearMonth actual y la primera fecha del mes
    val yearMonth = pageToYearMonth(page)
    val firstDayOfMonth = yearMonth.atDay(1)
    val daysInMonth = firstDayOfMonth.lengthOfMonth()
    val firstDayOfWeek = (firstDayOfMonth.dayOfWeek.value % 7)
    val previousMonth = yearMonth.minusMonths(1)
    val nextMonth = yearMonth.plusMonths(1)

    // Calculamos el número de filas necesarias para el mes
    val numRows = ((daysInMonth + firstDayOfWeek - 1) / 7) + 1

    // Calculamos el offset de la página actual para animaciones suaves
    val pageOffset = pagerState.getOffsetDistanceInPages(page).absoluteValue

    val coroutineScope: CoroutineScope = rememberCoroutineScope()

    // Creamos la columna con LazyColumn
    LazyColumn {
        items(numRows) { week ->
            // Ajustamos la altura de la fila según el desplazamiento de la página
            val height = if (week == 5) 40.dp * (1 - pageOffset) else 40.dp

            Row(
                modifier = Modifier.height(height),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Iteramos sobre los días de la semana
                (0 until 7).forEach { day ->
                    // Calculamos el índice del día en el mes actual
                    val dayIndex = week * 7 + day - firstDayOfWeek + 1
                    val date = when {
                        // Días dentro del mes actual
                        dayIndex in 1..daysInMonth -> firstDayOfMonth.plusDays(dayIndex.toLong() - 1)
                        // Días del mes anterior o siguiente
                        showAdjacentMonthDays -> {
                            when {
                                dayIndex < 1 -> previousMonth.atDay(previousMonth.lengthOfMonth() + dayIndex)
                                else -> nextMonth.atDay(dayIndex - daysInMonth)
                            }
                        }

                        else -> null
                    }

                    // Determinar si es un día extra
                    val isExtraDay = date != null && (date.month != yearMonth.month)

                    // Mostramos la celda según el tipo de fecha
                    if (date != null) {
                        DayCell(
                            date = date,
                            isSelected = date == selectedDate,
                            isToday = date == currentDay,
                            isExtraDay = isExtraDay,
                            onDateSelected = { it ->
                                onDateSelected(it)
                                if (showAdjacentMonthDays) {
                                    val targetPage =
                                        YearMonth.from(it).atDay(1).let { calculateInitialPage(it) }
                                    coroutineScope.launch {
                                        pagerState.animateScrollToPage(targetPage)
                                    }
                                }
                            },
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
    modifier: Modifier = Modifier,
    date: LocalDate,
    isSelected: Boolean,
    isToday: Boolean,
    isExtraDay: Boolean = false,
    onDateSelected: (LocalDate) -> Unit,
) {
    val backgroundColor by animateColorAsState(
        targetValue = when {
            isSelected -> colorScheme.primaryContainer
            isToday -> colorScheme.surfaceVariant
            else -> Color.Transparent
        },
        animationSpec = tween(durationMillis = 500), label = ""
    )

    val textColor by animateColorAsState(
        targetValue = when {
            isSelected -> colorScheme.inverseOnSurface
            isExtraDay -> colorScheme.onSurface.copy(alpha = 0.4f)
            else -> colorScheme.onBackground
        }, label = ""
    )

    CalendarCell(
        date = date,
        onDateSelected = onDateSelected,
        modifier = modifier
    ) {
        Text(
            text = date.dayOfMonth.toString(),
            color = textColor,
            modifier = Modifier
                .background(color = backgroundColor, shape = CircleShape)
                .size(30.dp)
                .wrapContentSize(align = Alignment.Center)
        )
    }
}