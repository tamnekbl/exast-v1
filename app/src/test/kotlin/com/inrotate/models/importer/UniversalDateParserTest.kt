package com.inrotate.models.importer

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test
import java.time.LocalDateTime

class UniversalDateParserTest {

    @Test
    fun `test single date with dd_MM_yyyy format`() {
        val rawDate = "01.01.2024"
        val expected = DateTimeRange(
            start = LocalDateTime.of(2024, 1, 1, 9, 0),
            end = LocalDateTime.of(2024, 1, 1, 18, 0)
        )
        assertEquals(expected, UniversalDateParser.parse(rawDate))
    }

    @Test
    fun `test single date with d_M_yyyy format`() {
        val rawDate = "1.1.2024"
        val expected = DateTimeRange(
            start = LocalDateTime.of(2024, 1, 1, 9, 0),
            end = LocalDateTime.of(2024, 1, 1, 18, 0)
        )
        assertEquals(expected, UniversalDateParser.parse(rawDate))
    }

    @Test
    fun `test single date with M_d_yy format`() {
        val rawDate = "1/2/24"
        val expected = DateTimeRange(
            start = LocalDateTime.of(2024, 1, 2, 9, 0),
            end = LocalDateTime.of(2024, 1, 2, 18, 0)
        )
        assertEquals(expected, UniversalDateParser.parse(rawDate))
    }

    @Test
    fun `test single date with M_d_yyyy format`() {
        val rawDate = "1/2/2024"
        val expected = DateTimeRange(
            start = LocalDateTime.of(2024, 1, 2, 9, 0),
            end = LocalDateTime.of(2024, 1, 2, 18, 0)
        )
        assertEquals(expected, UniversalDateParser.parse(rawDate))
    }

    @Test
    fun `test date range with normal hyphen`() {
        val rawDate = "01.01.2024-05.01.2024"
        val expected = DateTimeRange(
            start = LocalDateTime.of(2024, 1, 1, 9, 0),
            end = LocalDateTime.of(2024, 1, 5, 18, 0)
        )
        assertEquals(expected, UniversalDateParser.parse(rawDate))
    }

    @Test
    fun `test date range with en dash and spaces`() {
        val rawDate = "01.01.2024 – 05.01.2024"
        val expected = DateTimeRange(
            start = LocalDateTime.of(2024, 1, 1, 9, 0),
            end = LocalDateTime.of(2024, 1, 5, 18, 0)
        )
        assertEquals(expected, UniversalDateParser.parse(rawDate))
    }

    @Test
    fun `test date range with em dash and newlines`() {
        val rawDate = "01.01.2024\n—\n05.01.2024"
        val expected = DateTimeRange(
            start = LocalDateTime.of(2024, 1, 1, 9, 0),
            end = LocalDateTime.of(2024, 1, 5, 18, 0)
        )
        assertEquals(expected, UniversalDateParser.parse(rawDate))
    }

    @Test
    fun `test date range with mixed formats`() {
        val rawDate = "1.1.2024 - 5/1/2024"
        val expected = DateTimeRange(
            start = LocalDateTime.of(2024, 1, 1, 9, 0),
            end = LocalDateTime.of(2024, 5, 1, 18, 0)
        )
        assertEquals(expected, UniversalDateParser.parse(rawDate))
    }

    @Test
    fun `test blank input returns null`() {
        assertNull(UniversalDateParser.parse(" "))
    }

    @Test
    fun `test invalid date format returns null`() {
        assertNull(UniversalDateParser.parse("2024-01-01"))
    }

    @Test
    fun `test invalid date range returns null`() {
        assertNull(UniversalDateParser.parse("01.01.2024-"))
    }

    @Test
    fun `test invalid date value returns null`() {
        assertNull(UniversalDateParser.parse("32.01.2024"))
    }
}