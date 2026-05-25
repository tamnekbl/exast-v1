package com.inrotate.models.importer

import com.inrotate.models.Organization
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.io.File
import java.io.FileInputStream

class XlsxParserTest {

    private val testFilePath = "test_events.xlsx"
    private val realFiles = listOf("2022.xlsx", "2023.xlsx", "2024.xlsx", "2025.xlsx", "2026.xlsx")


    @BeforeEach
    fun setup() {
        XlsxTestFileCreator.createTestFile(testFilePath)
    }

    @AfterEach
    fun teardown() {
        File(testFilePath).delete()
    }

    @Test
    fun testMain() = runTest {
        //настраиваем файл для чтения
        val events = mutableListOf<EventRaw>()
        realFiles.forEach {
            val xlsxStream = this::class.java
                .classLoader
                .getResourceAsStream(it)
                ?: error("xlsx not found")
            events.addAll(XlsxParser.parseEvents(xlsxStream))
        }

        assertEquals(1421, events.size)
    }

    @Test
    fun `test parseEvents`() {
        val inputStream = FileInputStream(testFilePath)
        val events = XlsxParser.parseEvents(inputStream)

        assertEquals(1, events.size)
        val event = events[0]
        assertEquals("Test Event", event.title)
        assertEquals("2024-01-01", event.date)
        assertEquals("University", event.level)
        assertEquals("Conference", event.types)
        assertEquals("Online", event.location)
        assertEquals("Remote", event.format)
        assertEquals("иасид", event.organizations)
        assertEquals(10, event.participantsOther)
        assertEquals(20, event.participantsSpo)
        assertEquals(30, event.participantsVo)
        assertEquals(5, event.participantsForeign)
        assertEquals(65, event.participantsTotal)
        assertEquals("Organizer", event.organizationRole)
        assertEquals("Test Description", event.description)
    }

    @Test
    fun `test parseOrganizations with single valid alias`() {
        val organizationsRaw = "иасид"
        val expected = listOf(Organization(id = 0, name = "Институт архитектуры, строительства и дизайна КБГУ"))
        val result = XlsxParser.parseOrganizations(organizationsRaw)
        assertEquals(expected, result)
    }

    @Test
    fun `test parseOrganizations with multiple valid aliases`() {
        val organizationsRaw = "иасид, ииэир"
        val expected = listOf(
            Organization(id = 0, name = "Институт архитектуры, строительства и дизайна КБГУ"),
            Organization(id = 0, name = "Институт информатики, электроники и робототехники КБГУ")
        )
        val result = XlsxParser.parseOrganizations(organizationsRaw)
        assertEquals(expected, result)
    }

    @Test
    fun `test parseOrganizations with mixed case and whitespace`() {
        val organizationsRaw = "  ИАСиД  ,  ииэир "
        val expected = listOf(
            Organization(id = 0, name = "Институт архитектуры, строительства и дизайна КБГУ"),
            Organization(id = 0, name = "Институт информатики, электроники и робототехники КБГУ")
        )
        val result = XlsxParser.parseOrganizations(organizationsRaw)
        assertEquals(expected, result)
    }

    @Test
    fun `test parseOrganizations with duplicate aliases`() {
        val organizationsRaw = "иасид, иасид"
        val expected = listOf(Organization(id = 0, name = "Институт архитектуры, строительства и дизайна КБГУ"))
        val result = XlsxParser.parseOrganizations(organizationsRaw)
        assertEquals(expected, result)
    }

    @Test
    fun `test parseOrganizations with invalid alias`() {
        val organizationsRaw = "invalid_alias"
        assertThrows<IllegalArgumentException> {
            XlsxParser.parseOrganizations(organizationsRaw)
        }
    }

    @Test
    fun `test parseOrganizations with mixed valid and invalid aliases`() {
        val organizationsRaw = "иасид, invalid_alias"
        assertThrows<IllegalArgumentException> {
            XlsxParser.parseOrganizations(organizationsRaw)
        }
    }

    @Test
    fun `test parseOrganizations with empty string`() {
        val organizationsRaw = ""
        assertThrows<IllegalArgumentException> {
            XlsxParser.parseOrganizations(organizationsRaw)
        }
    }
}