package com.inrotate.models.importer

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter

object UniversalDateParser {

    private val singleFormats = listOf(
        DateTimeFormatter.ofPattern("dd.MM.yyyy"),
        DateTimeFormatter.ofPattern("d.M.yyyy"),
        DateTimeFormatter.ofPattern("M/d/yy"),
        DateTimeFormatter.ofPattern("M/d/yyyy")
    )
    private val defaultStartTime = LocalTime.of(9, 0)
    private val defaultEndTime = LocalTime.of(18, 0)

    fun parse(rawInput: String): DateTimeRange? {
        if (rawInput.isBlank()) return null

        // 1️⃣ нормализация строки
        val normalized = rawInput
            .replace("\n", "")
            .replace("–", "-")
            .replace("—", "-")
            .replace(" - ", "-")
            .trim()

        // 2️⃣ если диапазон
        if (normalized.contains("-")) {
            val parts = normalized.split("-")
                .map { it.trim() }
                .filter { it.isNotBlank() }

            if (parts.size == 2) {
                val start = parseSingle(parts[0]) ?: return null
                val end = parseSingle(parts[1]) ?: return null
                return DateTimeRange(start.atTime(defaultStartTime), end.atTime(defaultEndTime))
            }
        }

        // 3️⃣ одиночная дата
        val single = parseSingle(normalized) ?: return null
        return DateTimeRange(single.atTime(defaultStartTime), single.atTime(defaultEndTime))
    }

    private fun parseSingle(value: String): LocalDate? {
        for (formatter in singleFormats) {
            try {
                return LocalDate.parse(value, formatter)
            } catch (_: Exception) {
            }
        }
        return null
    }
}

data class DateTimeRange(
    val start: LocalDateTime,
    val end: LocalDateTime
)