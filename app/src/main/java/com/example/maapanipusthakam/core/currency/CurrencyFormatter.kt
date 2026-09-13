package com.example.maapanipusthakam.core.currency

object CurrencyFormatter {

    /**
     * Formats an integer amount (in rupees) to Indian numbering format (e.g., ₹1,000, ₹10,000, ₹1,25,000).
     */
    fun formatInRupees(amount: Long, includeSymbol: Boolean = true): String {
        val prefix = if (includeSymbol) "₹" else ""
        val isNegative = amount < 0
        val absAmount = if (isNegative) -amount else amount
        val formatted = formatIndianNumber(absAmount)
        return if (isNegative) "-$prefix$formatted" else "$prefix$formatted"
    }

    /**
     * Converts a number to Indian numbering comma groupings: 1,23,45,678
     */
    private fun formatIndianNumber(number: Long): String {
        val s = number.toString()
        if (s.length <= 3) return s

        val lastThree = s.substring(s.length - 3)
        val remaining = s.substring(0, s.length - 3)

        val sb = java.lang.StringBuilder()
        var count = 0
        for (i in remaining.length - 1 downTo 0) {
            sb.append(remaining[i])
            count++
            if (count % 2 == 0 && i > 0) {
                sb.append(',')
            }
        }
        val firstPart = sb.reverse().toString()
        return "$firstPart,$lastThree"
    }

    /**
     * Safely parse user input into a Long rupee amount.
     * Strips commas, spaces, currency symbols, and converts to Long.
     */
    fun parseAmount(input: String): Long? {
        val clean = input.replace(Regex("[^0-9]"), "")
        if (clean.isEmpty()) return null
        return try {
            clean.toLong()
        } catch (e: NumberFormatException) {
            null
        }
    }
}
