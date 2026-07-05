package com.example.util

import java.util.Calendar

object JalaliCalendar {
    val monthNamesFa = listOf(
        "فروردین", "اردیبهشت", "خرداد",
        "تیر", "مرداد", "شهریور",
        "مهر", "آبان", "آذر",
        "دی", "بهمن", "اسفند"
    )

    val monthNamesEn = listOf(
        "Farvardin", "Ordibehesht", "Khordad",
        "Tir", "Mordad", "Shahrivar",
        "Mehr", "Aban", "Azar",
        "Dey", "Bahman", "Esfand"
    )

    val weekdaysFa = listOf(
        "یکشنبه", "دوشنبه", "سه‌شنبه", "چهارشنبه", "پنجشنبه", "جمعه", "شنبه"
    )

    data class JalaliDate(val year: Int, val month: Int, val day: Int) {
        override fun toString(): String {
            return "$year/%02d/%02d".format(month, day)
        }
    }

    // Standard converter from Gregorian to Jalali
    fun g2j(gy: Int, gm: Int, gd: Int): JalaliDate {
        val gDaysInMonth = intArrayOf(0, 31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31)
        val jDaysInMonth = intArrayOf(0, 31, 31, 31, 31, 31, 31, 30, 30, 30, 30, 30, 29)

        val gy2 = gy - 1600
        val gm2 = gm - 1
        val gd2 = gd - 1

        var gDayNo = 365 * gy2 + gy2 / 4 - gy2 / 100 + gy2 / 400
        for (i in 0 until gm2) {
            gDayNo += gDaysInMonth[i + 1]
        }
        if (gm2 > 1 && ((gy % 4 == 0 && gy % 100 != 0) || gy % 400 == 0)) {
            gDayNo++
        }
        gDayNo += gd2

        var jDayNo = gDayNo - 79
        val jNp = jDayNo / 12053
        jDayNo %= 12053

        var jy = 979 + 33 * jNp + 4 * (jDayNo / 1461)
        jDayNo %= 1461

        if (jDayNo >= 366) {
            jy += (jDayNo - 1) / 365
            jDayNo = (jDayNo - 1) % 365
        }

        var jm = 0
        for (i in 0..11) {
            val days = if (i == 11 && (jy % 33) in listOf(1, 5, 9, 13, 17, 22, 26, 30)) 30 else jDaysInMonth[i + 1]
            if (jDayNo < days) {
                jm = i + 1
                break
            }
            jDayNo -= days
        }
        val jd = jDayNo + 1
        return JalaliDate(jy, jm, jd)
    }

    // Convert Milliseconds to JalaliDate
    fun getJalaliDateFromMillis(timeMs: Long): JalaliDate {
        val cal = Calendar.getInstance().apply { timeInMillis = timeMs }
        return g2j(
            cal.get(Calendar.YEAR),
            cal.get(Calendar.MONTH) + 1,
            cal.get(Calendar.DAY_OF_MONTH)
        )
    }

    // Convert Jalali year, month, day back to Gregorian Calendar
    fun j2g(jy: Int, jm: Int, jd: Int): Calendar {
        try {
            val safeJy = jy.coerceIn(1000, 3000)
            val safeJm = jm.coerceIn(1, 12)
            val safeJd = jd.coerceIn(1, 31)

            val jy2 = safeJy - 979
            val jm2 = safeJm - 1
            val jd2 = safeJd - 1

            var jDayNo = 365 * jy2 + (jy2 / 33) * 8 + (jy2 % 33 + 3) / 4
            for (i in 0 until jm2) {
                jDayNo += if (i < 6) 31 else 30
            }
            jDayNo += jd2

            var gDayNo = jDayNo + 792587
            var gy = 1600 + 400 * (gDayNo / 146097)
            gDayNo %= 146097

            var leap = true
            if (gDayNo >= 36525) {
                gDayNo--
                gy += 100 * (gDayNo / 36524)
                gDayNo %= 36524
                if (gDayNo >= 365) {
                    gDayNo++
                } else {
                    leap = false
                }
            }

            gy += 4 * (gDayNo / 1461)
            gDayNo %= 1461

            if (gDayNo >= 366) {
                leap = false
                gDayNo--
                gy += gDayNo / 365
                gDayNo %= 365
            }

            var gm = 0
            var gd = 0
            val gDaysInMonth = intArrayOf(0, 31, if (leap) 29 else 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31)
            for (i in 0..11) {
                val days = gDaysInMonth[i + 1]
                if (gDayNo < days) {
                    gm = i + 1
                    gd = gDayNo + 1
                    break
                }
                gDayNo -= days
            }

            // Fallback checks for leap-year edge cases
            val finalGm = if (gm <= 0) 12 else gm
            val finalGd = if (gd <= 0) 31 else gd

            return Calendar.getInstance().apply {
                set(Calendar.YEAR, gy)
                set(Calendar.MONTH, finalGm - 1)
                set(Calendar.DAY_OF_MONTH, finalGd)
                set(Calendar.HOUR_OF_DAY, 12)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }
        } catch (e: Exception) {
            // Unconditional safety fallback
            return Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, 12)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }
        }
    }

    // Convert to custom Persian digit representation
    fun String.toPersianDigits(): String {
        val englishDigits = '0'..'9'
        val persianDigits = listOf('۰', '۱', '۲', '۳', '۴', '۵', '۶', '۷', '۸', '۹')
        var result = this
        englishDigits.forEachIndexed { index, char ->
            result = result.replace(char, persianDigits[index])
        }
        return result
    }

    // Format full Jalali Date string nicely (e.g. "۱۴۰۵ خرداد ۰۶" or "farvardin 12, 1405" depending on language)
    fun formatJalali(timeMs: Long, displayInPersian: Boolean): String {
        try {
            val jd = getJalaliDateFromMillis(timeMs)
            val monthIdx = (jd.month - 1).coerceIn(0, 11)
            return if (displayInPersian) {
                val monthName = monthNamesFa[monthIdx]
                val yearStr = "${jd.year}".toPersianDigits()
                val dayStr = "${jd.day}".toPersianDigits()
                "$dayStr $monthName $yearStr"
            } else {
                val monthName = monthNamesEn[monthIdx]
                "$monthName ${jd.day}, ${jd.year}"
            }
        } catch (e: Exception) {
            return if (displayInPersian) "۱ فروردین ۱۴۰۵" else "Farvardin 1, 1405"
        }
    }

    // Converts Gregorian / solar time to standard format
    fun formatDateTimeJalali(timeMs: Long, displayInPersian: Boolean): String {
        val cal = java.util.Calendar.getInstance().apply { timeInMillis = timeMs }
        val hour = cal.get(java.util.Calendar.HOUR_OF_DAY)
        val minute = cal.get(java.util.Calendar.MINUTE)
        
        val datePart = formatJalali(timeMs, displayInPersian)
        
        val isPm = hour >= 12
        val displayHour = when {
            hour == 0 -> 12
            hour > 12 -> hour - 12
            else -> hour
        }
        val timePart = if (displayInPersian) {
            val pmAm = if (isPm) "ب.ظ" else "ق.ظ"
            val hourStr = "$displayHour".toPersianDigits()
            val minStr = "%02d".format(minute).toPersianDigits()
            "$hourStr:$minStr $pmAm"
        } else {
            val pmAm = if (isPm) "PM" else "AM"
            "$displayHour:%02d $pmAm".format(minute)
        }
        return "$datePart - $timePart"
    }

    // Converts digits inside text
    fun formatStringDigits(text: String, displayInPersian: Boolean): String {
        return if (displayInPersian) {
            text.toPersianDigits()
        } else {
            text
        }
    }
}
