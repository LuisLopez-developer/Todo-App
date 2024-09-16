package com.example.todoapp.addtasks.utils

import org.threeten.bp.LocalDate
import org.threeten.bp.LocalTime
import org.threeten.bp.format.DateTimeFormatter

object LocalDateSerializer {
    private val formatter = DateTimeFormatter.ISO_LOCAL_DATE

    fun serialize(localDate: LocalDate?): String? {
        return localDate?.format(formatter)
    }

    fun deserialize(dateString: String?): LocalDate? {
        return dateString?.let { LocalDate.parse(it, formatter) }
    }
}

object LocalTimeSerializer {
    private val formatter = DateTimeFormatter.ISO_LOCAL_TIME

    fun serialize(localTime: LocalTime?): String? {
        return localTime?.format(formatter)
    }

    fun deserialize(timeString: String?): LocalTime? {
        return timeString?.let { LocalTime.parse(it, formatter) }
    }
}