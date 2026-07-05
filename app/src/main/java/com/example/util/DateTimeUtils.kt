package com.example.util

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

object DateTimeUtils {
    private val dateFormatter = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
    private val timeFormatter = SimpleDateFormat("hh:mm a", Locale.getDefault())
    private val dateTimeFormatter = SimpleDateFormat("MMM dd, yyyy hh:mm a", Locale.getDefault())

    fun formatDate(timestamp: Long): String = dateFormatter.format(Date(timestamp))
    fun formatTime(timestamp: Long): String = timeFormatter.format(Date(timestamp))
    fun formatDateTime(timestamp: Long): String = dateTimeFormatter.format(Date(timestamp))

    fun isSameDay(time1: Long, time2: Long): Boolean {
        val cal1 = Calendar.getInstance().apply { timeInMillis = time1 }
        val cal2 = Calendar.getInstance().apply { timeInMillis = time2 }
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR)
    }

    fun isSameWeek(time1: Long, time2: Long): Boolean {
        val cal1 = Calendar.getInstance().apply { timeInMillis = time1 }
        val cal2 = Calendar.getInstance().apply { timeInMillis = time2 }
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.WEEK_OF_YEAR) == cal2.get(Calendar.WEEK_OF_YEAR)
    }

    fun getStartOfDay(time: Long): Long {
        return Calendar.getInstance().apply {
            timeInMillis = time
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis
    }

    fun getDaysOfWeek(time: Long): List<Long> {
        val cal = Calendar.getInstance().apply {
            timeInMillis = time
            set(Calendar.DAY_OF_WEEK, firstDayOfWeek)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val days = mutableListOf<Long>()
        for (i in 0..6) {
            days.add(cal.timeInMillis)
            cal.add(Calendar.DAY_OF_MONTH, 1)
        }
        return days
    }
}
