package com.inrotate.analytics.ai.dataset

import com.inrotate.analytics.FakeEventRepository
import com.inrotate.analytics.testEvent
import com.inrotate.analytics.testOrganization
import com.inrotate.models.EventType
import kotlinx.coroutines.runBlocking
import java.nio.charset.StandardCharsets
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class AiTrainingDatasetBuilderTest {
    @Test
    fun `buildCsv creates required columns and one row per event`() = runBlocking {
        val builder = AiTrainingDatasetBuilder(
            FakeEventRepository(
                listOf(
                    testEvent(id = 1),
                    testEvent(id = 2, participantsTotal = 0),
                    testEvent(id = 3),
                ),
            ),
        )

        val lines = builder.buildCsv().toString(StandardCharsets.UTF_8)
            .lineSequence()
            .filter { it.isNotBlank() }
            .toList()

        assertEquals(REQUIRED_HEADER, parseCsvLine(lines.first()))
        assertEquals(3, lines.size)
    }

    @Test
    fun `buildCsv includes participants_total and json encoded organizations and types`() = runBlocking {
        val builder = AiTrainingDatasetBuilder(
            FakeEventRepository(
                listOf(
                    testEvent(
                        participantsTotal = 120,
                        types = listOf(EventType.cultural_creative, EventType.physical),
                        organizations = listOf(testOrganization(id = 49, isExternal = true)),
                    ),
                ),
            ),
        )

        val lines = builder.buildCsv().toString(StandardCharsets.UTF_8)
            .lineSequence()
            .filter { it.isNotBlank() }
            .toList()
        val row = parseCsvLine(lines[1])
        val valuesByColumn = REQUIRED_HEADER.zip(row).toMap()

        assertEquals("120", valuesByColumn.getValue("participants_total"))
        assertEquals("""["cultural_creative","physical"]""", valuesByColumn.getValue("types"))
        assertTrue(valuesByColumn.getValue("organizations").contains(""""isExternal":true"""))
        assertTrue(valuesByColumn.getValue("organizations").startsWith("[{"))
    }

    private fun parseCsvLine(line: String): List<String> {
        val result = mutableListOf<String>()
        val current = StringBuilder()
        var quoted = false
        var index = 0

        while (index < line.length) {
            val char = line[index]
            when {
                char == '"' && quoted && index + 1 < line.length && line[index + 1] == '"' -> {
                    current.append('"')
                    index++
                }

                char == '"' -> quoted = !quoted
                char == ',' && !quoted -> {
                    result.add(current.toString())
                    current.clear()
                }

                else -> current.append(char)
            }
            index++
        }
        result.add(current.toString())
        return result
    }

    private companion object {
        val REQUIRED_HEADER = listOf(
            "title",
            "description",
            "date_start",
            "date_end",
            "time_start",
            "time_end",
            "level",
            "format",
            "organization_role",
            "types",
            "organizations",
            "participants_total",
            "participants_vo",
            "participants_spo",
            "participants_foreign",
            "participants_other",
        )
    }
}
