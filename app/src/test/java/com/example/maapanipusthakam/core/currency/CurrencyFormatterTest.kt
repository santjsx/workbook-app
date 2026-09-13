package com.example.maapanipusthakam.core.currency

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class CurrencyFormatterTest {

    @Test
    fun testIndianCurrencyFormatting() {
        assertEquals("₹0", CurrencyFormatter.formatInRupees(0))
        assertEquals("₹1", CurrencyFormatter.formatInRupees(1))
        assertEquals("₹999", CurrencyFormatter.formatInRupees(999))
        assertEquals("₹1,000", CurrencyFormatter.formatInRupees(1000))
        assertEquals("₹10,000", CurrencyFormatter.formatInRupees(10000))
        assertEquals("₹1,00,000", CurrencyFormatter.formatInRupees(100000))
        assertEquals("₹10,00,000", CurrencyFormatter.formatInRupees(1000000))
        assertEquals("₹1,23,45,678", CurrencyFormatter.formatInRupees(12345678))
    }

    @Test
    fun testNegativeCurrencyFormatting() {
        assertEquals("-₹500", CurrencyFormatter.formatInRupees(-500))
        assertEquals("-₹1,25,000", CurrencyFormatter.formatInRupees(-125000))
    }

    @Test
    fun testAmountParsing() {
        assertEquals(5000L, CurrencyFormatter.parseAmount("5000"))
        assertEquals(5000L, CurrencyFormatter.parseAmount("₹ 5,000"))
        assertEquals(125000L, CurrencyFormatter.parseAmount("1,25,000"))
        assertNull(CurrencyFormatter.parseAmount(""))
        assertNull(CurrencyFormatter.parseAmount("abc"))
    }
}
