package com.example.maapanipusthakam.core.date

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate

class DateFormatterTest {

    @Test
    fun testTeluguDateFormatting() {
        val date = LocalDate.of(2026, 9, 13).toEpochDay()
        assertEquals("13 సెప్టెంబర్ 2026", DateFormatter.formatToTeluguDate(date))

        val janDate = LocalDate.of(2026, 1, 1).toEpochDay()
        assertEquals("1 జనవరి 2026", DateFormatter.formatToTeluguDate(janDate))

        val decDate = LocalDate.of(2026, 12, 31).toEpochDay()
        assertEquals("31 డిసెంబర్ 2026", DateFormatter.formatToTeluguDate(decDate))
    }

    @Test
    fun testRelativeTeluguDates() {
        val today = DateFormatter.todayEpochDay()
        assertEquals("ఈ రోజు", DateFormatter.formatShortTeluguDate(today))
        assertEquals("నిన్న", DateFormatter.formatShortTeluguDate(today - 1))
        assertEquals("రేపు", DateFormatter.formatShortTeluguDate(today + 1))
    }

    @Test
    fun testFutureDateDetection() {
        val today = DateFormatter.todayEpochDay()
        assertFalse(DateFormatter.isFutureDate(today))
        assertFalse(DateFormatter.isFutureDate(today - 5))
        assertTrue(DateFormatter.isFutureDate(today + 1))
        assertTrue(DateFormatter.isFutureDate(today + 100))
    }
}
