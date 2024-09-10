package com.example.todoapp.addtasks.ui.utils

import org.threeten.bp.LocalDate
import org.threeten.bp.LocalTime
import org.threeten.bp.format.DateTimeFormatter
import java.util.Locale

fun formatDate(date: LocalDate, locale: Locale = Locale("es", "PE")): String {
    val formatter = DateTimeFormatter.ofPattern("E, dd 'de' MMM 'de' yyyy", locale)
    return date.format(formatter)
}

fun formatTime(time: LocalTime, locale: Locale = Locale("es", "PE")): String {
    val timeFormatter = DateTimeFormatter.ofPattern("hh:mm a", locale)
    return time.format(timeFormatter)
}