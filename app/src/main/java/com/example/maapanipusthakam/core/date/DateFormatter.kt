package com.example.maapanipusthakam.core.date

import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

object DateFormatter {

    private val teluguMonths = arrayOf(
        "జనవరి",
        "ఫిబ్రవరి",
        "మార్చి",
        "ఏప్రిల్",
        "మే",
        "జూన్",
        "జూలై",
        "ఆగస్టు",
        "సెప్టెంబర్",
        "అక్టోబర్",
        "నవంబర్",
        "డిసెంబర్"
    )

    /**
     * Formats an epoch day to Telugu date string, e.g. "13 సెప్టెంబర్ 2026"
     */
    fun formatToTeluguDate(epochDay: Long): String {
        val date = LocalDate.ofEpochDay(epochDay)
        val day = date.dayOfMonth
        val monthName = teluguMonths[date.monthValue - 1]
        val year = date.year
        return "$day $monthName $year"
    }

    /**
     * Short format without year if current year, or full
     */
    fun formatShortTeluguDate(epochDay: Long): String {
        val date = LocalDate.ofEpochDay(epochDay)
        val day = date.dayOfMonth
        val monthName = teluguMonths[date.monthValue - 1]
        val today = todayEpochDay()
        val diff = epochDay - today

        return when (diff) {
            0L -> "ఈ రోజు"
            -1L -> "నిన్న"
            1L -> "రేపు"
            else -> "$day $monthName"
        }
    }

    /**
     * Returns today's epoch day according to the system default local timezone
     */
    fun todayEpochDay(): Long {
        return LocalDate.now(ZoneId.systemDefault()).toEpochDay()
    }

    /**
     * Current system timestamp in milliseconds
     */
    fun currentTimeMillis(): Long {
        return System.currentTimeMillis()
    }

    /**
     * Check if a given date is in the future
     */
    fun isFutureDate(epochDay: Long): Boolean {
        return epochDay > todayEpochDay()
    }
}
